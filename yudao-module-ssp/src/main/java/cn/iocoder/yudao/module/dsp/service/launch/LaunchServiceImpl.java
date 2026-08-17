package cn.iocoder.yudao.module.dsp.service.launch;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import java.util.stream.Collectors;
import cn.iocoder.yudao.module.dsp.controller.admin.launch.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.LAUNCH_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 媒体预算绑定 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class LaunchServiceImpl implements LaunchService {

    @Resource
    private LaunchMapper launchMapper;

    @Resource
    private EtcdClient etcdClient;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createLaunch(LaunchSaveReqVO createReqVO) {
        // 插入
        LaunchDO launch = BeanUtils.toBean(createReqVO, LaunchDO.class);
        launchMapper.insert(launch);

        // 同步到etcd
        syncToEtcd(launch);

        // 返回
        return launch.getId();
    }

    @Override
    public void updateLaunch(LaunchSaveReqVO updateReqVO) {
        // 校验存在
        validateLaunchExists(updateReqVO.getId());
        // 更新
        LaunchDO updateObj = BeanUtils.toBean(updateReqVO, LaunchDO.class);
        launchMapper.updateById(updateObj);

        // 同步到etcd
        syncToEtcd(updateObj);
    }

    @Override
    public void deleteLaunch(Long id) {
        // 校验存在
        validateLaunchExists(id);
        // 删除
        launchMapper.deleteById(id);

        // 从etcd删除
        deleteFromEtcd(id);
    }

    @Override
        public void deleteLaunchListByIds(List<Long> ids) {
        // 删除
        launchMapper.deleteByIds(ids);

        // 批量从etcd删除
        for (Long id : ids) {
            deleteFromEtcd(id);
        }
        }


    private void validateLaunchExists(Long id) {
        if (launchMapper.selectById(id) == null) {
            throw exception(LAUNCH_NOT_EXISTS);
        }
    }

    @Override
    public LaunchDO getLaunch(Long id) {
        return launchMapper.selectById(id);
    }

    @Override
    public PageResult<LaunchDO> getLaunchPage(LaunchPageReqVO pageReqVO) {
        return launchMapper.selectPage(pageReqVO);
    }

    @Override
    public List<LaunchDO> getLaunchSspSlotIdQuery(Long sspSlotId) {
        // 1. 查询该 sspSlotId 关联的所有 Launch 记录
        List<LaunchDO> launchList = launchMapper.selectLaunchBySspSlotId(sspSlotId);

        if (CollUtil.isEmpty(launchList)) {
            return Collections.emptyList();
        }

        // 2. 查询关联的 DSP 广告位信息
        List<DspSlotInfoDO> dspSlotInfoList = launchMapper.selectDspSlotInfoBySspSlotId(sspSlotId);

        if (CollUtil.isEmpty(dspSlotInfoList)) {
            return launchList;
        }

        // 3. 将 DSP 广告位信息按 ID 分组，便于快速查找
        Map<Long, DspSlotInfoDO> dspSlotInfoMap = dspSlotInfoList.stream()
                .collect(Collectors.toMap(DspSlotInfoDO::getId, dsp -> dsp));

        // 4. 为每个 Launch 对象填充关联的 DSP 广告位信息
        for (LaunchDO launch : launchList) {
            DspSlotInfoDO dspSlotInfo = dspSlotInfoMap.get(launch.getDspSlotId());
            if (dspSlotInfo != null) {
                launch.setDspSlotInfoDO(Collections.singletonList(dspSlotInfo));
            } else {
                launch.setDspSlotInfoDO(Collections.emptyList());
            }
        }

        return launchList;
    }

    @Override
    public List<LaunchDO> getLaunchDspSlotIdQuery(Long id) {
        // 1. 查询该 dspSlotId 关联的所有 Launch 记录
        List<LaunchDO> launchList = launchMapper.selectLaunchByDspSlotId(id);

        if (CollUtil.isEmpty(launchList)) {
            return Collections.emptyList();
        }

        // 2. 查询关联的 SSP 广告位信息
        List<SspSlotInfoDO> sspSlotInfoList = launchMapper.selectSspSlotInfoByDspSlotId(id);

        if (CollUtil.isEmpty(sspSlotInfoList)) {
            return launchList;
        }

        // 3. 将 SSP 广告位信息按 ID 分组，便于快速查找
        Map<Long, SspSlotInfoDO> sspSlotInfoMap = sspSlotInfoList.stream()
                .collect(Collectors.toMap(SspSlotInfoDO::getId, ssp -> ssp));

        // 4. 为每个 Launch 对象填充关联的 SSP 广告位信息
        for (LaunchDO launch : launchList) {
            SspSlotInfoDO sspSlotInfo = sspSlotInfoMap.get(launch.getSspSlotId());
            if (sspSlotInfo != null) {
                launch.setSspSlotInfoDo(Collections.singletonList(sspSlotInfo));
            } else {
                launch.setSspSlotInfoDo(Collections.emptyList());
            }
        }

        return launchList;
    }

    @Override
    public List<LaunchDO> getLaunchSspSlotList(Long id) {
        List<LaunchDO> launchSspSlotList = launchMapper.getLaunchSspSlotList(id);
        return launchSspSlotList;
    }

    /**
     * 同步数据到etcd
     *
     * @param launch 媒体预算关联信息
     */
    private void syncToEtcd(LaunchDO launch) {
        try {
            // 构建etcd key: {etcdPrefix}/launch/{id}
            String etcdKey = etcdPrefix + "/launch/" + launch.getId();

            // 构建符合DSP API要求的JSON格式
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", launch.getId());
            etcdData.put("ssp_slot_id", launch.getSspSlotId() != null ? launch.getSspSlotId() : 0);
            etcdData.put("dsp_slot_id", launch.getDspSlotId() != null ? launch.getDspSlotId() : 0);
            etcdData.put("traffic_weight", launch.getTrafficWeight() != null ? launch.getTrafficWeight() : 100);
            etcdData.put("traffic_group", launch.getTrafficGroup() != null ? launch.getTrafficGroup() : 1);
            etcdData.put("floor_price", launch.getFloorPrice() != null ? launch.getFloorPrice() : 0);
            etcdData.put("dsp_pay_ratio", launch.getDspPayRatio()); // 默认值，可根据需要调整

            etcdData.put("launch_hour", launch.getLaunchHour()); // 默认值
            etcdData.put("launch_time", launch.getLogTime() != null ? launch.getLogTime() : System.currentTimeMillis());
            etcdData.put("log_time", launch.getLogTime()); // 默认值
            etcdData.put("req", launch.getReq()); // 默认值
            etcdData.put("ims",launch.getIms()); // 默认值
            etcdData.put("clk", launch.getClk());
            etcdData.put("pkg_trans",launch.getPkgTrans());

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.put(etcdKey, etcdValue);

            log.info("Launch 同步到etcd成功, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("Launch 同步到etcd失败, id: {}", launch.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id 媒体预算关联ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/launch/" + id;
            etcdClient.delete(etcdKey);
            log.info("Launch 从etcd删除成功, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("Launch 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }


}