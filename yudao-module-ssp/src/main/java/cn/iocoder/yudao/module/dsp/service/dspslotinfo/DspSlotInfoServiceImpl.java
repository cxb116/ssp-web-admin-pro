package cn.iocoder.yudao.module.dsp.service.dspslotinfo;

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
import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_HAS_LAUNCH;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_INFO_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 预算广告位 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class DspSlotInfoServiceImpl implements DspSlotInfoService {

    @Resource
    private DspSlotInfoMapper slotInfoMapper;

    @Resource
    private LaunchMapper launchMapper;

    @Resource
    private EtcdClient etcdClient;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createSlotInfo(DspSlotInfoSaveReqVO createReqVO) {
        // 插入
        DspSlotInfoDO slotInfo = BeanUtils.toBean(createReqVO, DspSlotInfoDO.class);
        slotInfoMapper.insert(slotInfo);

        // 同步到etcd
        syncToEtcd(slotInfo);

        // 返回
        return slotInfo.getId();
    }

    @Override
    public void updateSlotInfo(DspSlotInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateSlotInfoExists(updateReqVO.getId());
        // 更新
        DspSlotInfoDO updateObj = BeanUtils.toBean(updateReqVO, DspSlotInfoDO.class);
        slotInfoMapper.updateById(updateObj);

        // 同步到etcd
        syncToEtcd(updateObj);
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
        if (slotInfoMapper.selectById(id) == null) {
            throw exception(DSP_SLOT_INFO_NOT_EXISTS);
        }
    }

    private void validateSlotInfoHasNoLaunch(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            List<LaunchDO> launches = launchMapper.selectLaunchByDspSlotId(id);
            if (CollUtil.isNotEmpty(launches)) {
                throw exception(DSP_SLOT_HAS_LAUNCH);
            }
        }
    }

    @Override
    public DspSlotInfoDO getSlotInfo(Long id) {
        return slotInfoMapper.selectById(id);
    }

    @Override
    public PageResult<DspSlotInfoDO> getSlotInfoPage(DspSlotInfoPageReqVO pageReqVO) {
        Long offset = (pageReqVO.getPageNo() - 1L) * pageReqVO.getPageSize();
        List<DspSlotInfoDO> list = slotInfoMapper.selectPage(pageReqVO, offset, pageReqVO.getPageSize());
        Long total = slotInfoMapper.selectPageCount(pageReqVO);
        return new PageResult<>(list, total);
    }



    /**
     * 同步数据到etcd
     *
     * @param slotInfo DSP广告位信息
     */
    private void syncToEtcd(DspSlotInfoDO slotInfo) {
        try {
            // 构建etcd key: {etcdPrefix}/dsp/{id}
            String etcdKey = etcdPrefix + "/dsp/" + slotInfo.getId();

            // 构建符合DSP API要求的JSON格式
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", slotInfo.getId());
            etcdData.put("name", slotInfo.getName() != null ? slotInfo.getName() : "");
            etcdData.put("dsp_slot_code", slotInfo.getDspSlotCode() != null ? slotInfo.getDspSlotCode() : "");
            etcdData.put("product_name", slotInfo.getProductName() != null ? slotInfo.getProductName() : "");
            etcdData.put("company_id", slotInfo.getCompanyId() != null ? slotInfo.getCompanyId() : 0);
            etcdData.put("product_id", slotInfo.getProductId() != null ? slotInfo.getProductId() : 0);
            etcdData.put("ad_type_id", slotInfo.getAdScene() != null ? slotInfo.getAdScene() : 0);
            etcdData.put("os_type", slotInfo.getOsType() != null ? slotInfo.getOsType() : 0);
            etcdData.put("dsp_app_key", slotInfo.getDspAppKey() != null ? slotInfo.getDspAppKey() : "");
            etcdData.put("dsp_app_id", slotInfo.getDspAppId() != null ? slotInfo.getDspAppId() : "");
            etcdData.put("dsp_app_pkg", slotInfo.getDspAppPkg() != null ? slotInfo.getDspAppPkg() : "");
            etcdData.put("dsp_app_ver", slotInfo.getDspAppVer() != null ? slotInfo.getDspAppVer() : "");
            etcdData.put("dsp_app_store_ver", slotInfo.getDspAppStoreVer() != null ? slotInfo.getDspAppStoreVer() : "");
            etcdData.put("price_encrypt_key", slotInfo.getPriceEncryptKey() != null ? slotInfo.getPriceEncryptKey() : "");
            etcdData.put("dsp_app_store_link", slotInfo.getDspAppStoreLink() != null ? slotInfo.getDspAppStoreLink() : "");
            etcdData.put("dsp_pay_type", slotInfo.getDspPayType() != null ? slotInfo.getDspPayType() : 0);
            etcdData.put("dsp_deal_ratio", 0.7); // 默认值，可根据实际需求调整

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.put(etcdKey, etcdValue);

            log.info("DspSlotInfo 同步到etcd成功, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("DspSlotInfo 同步到etcd失败, id: {}", slotInfo.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id DSP广告位ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/dsp/" + id;
            etcdClient.delete(etcdKey);
            log.info("DspSlotInfo 从etcd删除成功, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("DspSlotInfo 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }

}
