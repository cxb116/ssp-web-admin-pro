package cn.iocoder.yudao.module.ssp.service.sspSlotInfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SLOT_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SSP_SLOT_HAS_LAUNCH;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 媒体广告位 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class SspSlotInfoServiceImpl implements SspSlotInfoService {

    @Resource
    private SspSlotInfoMapper slotInfoMapper;

    @Resource
    private LaunchMapper launchMapper;

    @Resource
    private EtcdClient etcdClient;

    @Resource
    private SspSlotDayMapper spSlotDayMapper;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createSlotInfo(SspSlotInfoSaveReqVO createReqVO) {
        // 插入
        SspSlotInfoDO slotInfo = BeanUtils.toBean(createReqVO, SspSlotInfoDO.class);
        slotInfoMapper.insert(slotInfo);

        // osType 来自 ssp_app 关联字段，需重新查询完整数据后再同步 etcd
        SspSlotInfoDO fullSlotInfo = slotInfoMapper.selectSlotInfoById(slotInfo.getId());
        syncToEtcd(fullSlotInfo != null ? fullSlotInfo : slotInfo);

        // 返回
        return slotInfo.getId();
    }

    @Override
    public void updateSlotInfo(SspSlotInfoUpdateReqVO updateReqVO) {
        // 校验存在
        validateSlotInfoExists(updateReqVO.getId());
        // 更新
        SspSlotInfoDO updateObj = BeanUtils.toBean(updateReqVO, SspSlotInfoDO.class);
        slotInfoMapper.updateById(updateObj);

        // 更新请求不包含 osType（来自应用表），直接用 updateObj 同步会导致 os_type=0
        // 重新查询关联数据（含 app.os_type）后再同步 etcd
        SspSlotInfoDO fullSlotInfo = slotInfoMapper.selectSlotInfoById(updateReqVO.getId());
        if (fullSlotInfo != null) {
            syncToEtcd(fullSlotInfo);
        } else {
            syncToEtcd(updateObj);
        }
    }

    @Override
    public void deleteSlotInfo(Long id) {
        // 校验存在
        validateSlotInfoExists(id);
        validateSlotInfoHasNoLaunch(Collections.singletonList(id));
        // 删除
        slotInfoMapper.deleteById(id);

        // 从etcd删除
        deleteFromEtcd(id);
    }

    @Override
        public void deleteSlotInfoListByIds(List<Long> ids) {
        validateSlotInfoHasNoLaunch(ids);
        // 删除
        slotInfoMapper.deleteByIds(ids);

        // 批量从etcd删除
        for (Long id : ids) {
            deleteFromEtcd(id);
        }
        }


    private void validateSlotInfoExists(Long id) {
        if (slotInfoMapper.selectSlotInfoById(id) == null) {
            throw exception(SLOT_INFO_NOT_EXISTS);
        }
    }

    private void validateSlotInfoHasNoLaunch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            List<LaunchDO> launches = launchMapper.selectLaunchBySspSlotId(id);
            if (CollUtil.isNotEmpty(launches)) {
                throw exception(SSP_SLOT_HAS_LAUNCH);
            }
        }
    }

    @Override
    public SspSlotInfoDO getSlotInfo(Long id) {
        return slotInfoMapper.selectSlotInfoById(id);
    }

    @Override
    public PageResult<SspSlotInfoDO> getSlotInfoPage(SspSlotInfoPageReqVO pageReqVO) {

        String time = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMdd"));
        Long offset = (pageReqVO.getPageNo() - 1L) * pageReqVO.getPageSize();
        List<SspSlotInfoDO> list = slotInfoMapper.selectPage(pageReqVO, offset, pageReqVO.getPageSize());
        for (SspSlotInfoDO sspSlotInfo : list) {
            Long id = sspSlotInfo.getId();
            List<LaunchDO> launchDOS = launchMapper.selectLaunchBySspSlotId(id);
            sspSlotInfo.setLs(launchDOS.size());

            SspSlotDayDO sspSlotDayDO = spSlotDayMapper.selectRequestCount(sspSlotInfo.getId(), time);
            if (sspSlotDayDO != null) {
                sspSlotInfo.setReqCount(sspSlotDayDO.getReqCount());
            }


        }

        Long total = slotInfoMapper.selectPageCount(pageReqVO);
        return new PageResult<>(list, total);
    }

    /**
     * 同步数据到etcd
     *
     * @param slotInfo 广告位信息
     */
    private void syncToEtcd(SspSlotInfoDO slotInfo) {
        try {
            // 构建etcd key: {etcdPrefix}/sspslot/{id}
            String etcdKey = etcdPrefix + "/sspslot/" + slotInfo.getId();

            // 构建符合DSP API要求的JSON格式
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", slotInfo.getId());
            etcdData.put("ad_scene", slotInfo.getAdScene() != null ? slotInfo.getAdScene() : 0);
            etcdData.put("ssp_pay_type", slotInfo.getSspPayType() != null ? slotInfo.getSspPayType() : 0);
            etcdData.put("ssp_deal_ratio", slotInfo.getSspDealRatio() != null ?
                slotInfo.getSspDealRatio() : 0); // 将整数转换为百分比
            // osType 为关联字段（ssp_app.os_type），同步前应使用 selectSlotInfoById 查全量
            etcdData.put("os_type", slotInfo.getOsType());
            etcdData.put("app_id", slotInfo.getAppId() != null ? slotInfo.getAppId() : 0);

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.putAsync(etcdKey, etcdValue);

            log.info("SspSlotInfo etcd sync task submitted, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("SspSlotInfo 同步到etcd失败, id: {}", slotInfo.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id 广告位ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/sspslot/" + id;
            etcdClient.deleteAsync(etcdKey);
            log.info("SspSlotInfo etcd delete task submitted, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("SspSlotInfo 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }

}
