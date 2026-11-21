package cn.iocoder.yudao.module.ssp.service.slotblacklist;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklist.SlotBlacklistDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.ssp.dal.mysql.slotblacklist.SlotBlacklistMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SLOT_BLACKLIST_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 黑名单配置 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class SlotBlacklistServiceImpl implements SlotBlacklistService {

    @Resource
    private SlotBlacklistMapper slotBlacklistMapper;

    @Resource
    private EtcdClient etcdClient;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createSlotBlacklist(SlotBlacklistSaveReqVO createReqVO) {
        // 插入
        SlotBlacklistDO slotBlacklist = BeanUtils.toBean(createReqVO, SlotBlacklistDO.class);
        slotBlacklistMapper.insert(slotBlacklist);

        // 同步到etcd
        syncToEtcd(slotBlacklist);

        // 返回
        return slotBlacklist.getId();
    }

    @Override
    public void updateSlotBlacklist(SlotBlacklistSaveReqVO updateReqVO) {
        // 校验存在
        validateSlotBlacklistExists(updateReqVO.getId());
        // 更新
        SlotBlacklistDO updateObj = BeanUtils.toBean(updateReqVO, SlotBlacklistDO.class);
        slotBlacklistMapper.updateById(updateObj);

        // 更新后重新查询完整数据，再同步etcd，避免 null 字段覆盖
        SlotBlacklistDO fullBlacklist = slotBlacklistMapper.selectById(updateReqVO.getId());
        syncToEtcd(fullBlacklist != null ? fullBlacklist : updateObj);
    }

    @Override
    public void deleteSlotBlacklist(Long id) {
        // 校验存在
        validateSlotBlacklistExists(id);
        // 删除
        slotBlacklistMapper.deleteById(id);

        // 从etcd删除
        deleteFromEtcd(id);
    }

    @Override
        public void deleteSlotBlacklistListByIds(List<Long> ids) {
        // 删除
        slotBlacklistMapper.deleteByIds(ids);

        // 批量从etcd删除
        for (Long id : ids) {
            deleteFromEtcd(id);
        }
        }


    private void validateSlotBlacklistExists(Long id) {
        if (slotBlacklistMapper.selectById(id) == null) {
            throw exception(SLOT_BLACKLIST_NOT_EXISTS);
        }
    }

    @Override
    public SlotBlacklistDO getSlotBlacklist(Long id) {
        return slotBlacklistMapper.selectById(id);
    }

    @Override
    public PageResult<SlotBlacklistDO> getSlotBlacklistPage(SlotBlacklistPageReqVO pageReqVO) {
        return slotBlacklistMapper.selectPage(pageReqVO);
    }

    /**
     * 同步数据到etcd，只同步 DSP 侧需要的四个字段：id、media_id、backlist_domains、status
     *
     * @param slotBlacklist 黑名单配置
     */
    private void syncToEtcd(SlotBlacklistDO slotBlacklist) {
        try {
            // 构建etcd key: {etcdPrefix}/blacklist/{id}
            String etcdKey = etcdPrefix + "/blacklist/" + slotBlacklist.getId();

            // 构建符合DSP API要求的JSON格式（snake_case）
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", slotBlacklist.getId());
            etcdData.put("media_id", slotBlacklist.getMediaId() != null ? slotBlacklist.getMediaId() : 0);
            etcdData.put("backlist_domains", slotBlacklist.getBacklistDomains() != null ? slotBlacklist.getBacklistDomains() : "");
            // status 库中为字符串（"1"/"2"），DSP 侧结构体是 int：数值型转数字下发，保证可直接反序列化
            Object statusValue = 0;
            if (slotBlacklist.getStatus() != null && !slotBlacklist.getStatus().trim().isEmpty()) {
                try {
                    statusValue = Integer.parseInt(slotBlacklist.getStatus().trim());
                } catch (NumberFormatException e) {
                    statusValue = slotBlacklist.getStatus();
                }
            }
            etcdData.put("status", statusValue);

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.putAsync(etcdKey, etcdValue);

            log.info("SlotBlacklist etcd sync task submitted, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("SlotBlacklist 同步到etcd失败, id: {}", slotBlacklist.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id 黑名单配置ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/blacklist/" + id;
            etcdClient.deleteAsync(etcdKey);
            log.info("SlotBlacklist etcd delete task submitted, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("SlotBlacklist 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }

}