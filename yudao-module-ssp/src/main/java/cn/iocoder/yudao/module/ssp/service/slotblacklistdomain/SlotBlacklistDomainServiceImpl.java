package cn.iocoder.yudao.module.ssp.service.slotblacklistdomain;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklistdomain.SlotBlacklistDomainDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.ssp.dal.mysql.slotblacklistdomain.SlotBlacklistDomainMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SLOT_BLACKLIST_DOMAIN_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 黑名单统计 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SlotBlacklistDomainServiceImpl implements SlotBlacklistDomainService {

    @Resource
    private SlotBlacklistDomainMapper slotBlacklistDomainMapper;

    @Override
    public Long createSlotBlacklistDomain(SlotBlacklistDomainSaveReqVO createReqVO) {
        // 插入
        SlotBlacklistDomainDO slotBlacklistDomain = BeanUtils.toBean(createReqVO, SlotBlacklistDomainDO.class);
        slotBlacklistDomainMapper.insert(slotBlacklistDomain);

        // 返回
        return slotBlacklistDomain.getId();
    }

    @Override
    public void updateSlotBlacklistDomain(SlotBlacklistDomainSaveReqVO updateReqVO) {
        // 校验存在
        validateSlotBlacklistDomainExists(updateReqVO.getId());
        // 更新
        SlotBlacklistDomainDO updateObj = BeanUtils.toBean(updateReqVO, SlotBlacklistDomainDO.class);
        slotBlacklistDomainMapper.updateById(updateObj);
    }

    @Override
    public void deleteSlotBlacklistDomain(Long id) {
        // 校验存在
        validateSlotBlacklistDomainExists(id);
        // 删除
        slotBlacklistDomainMapper.deleteById(id);
    }

    @Override
        public void deleteSlotBlacklistDomainListByIds(List<Long> ids) {
        // 删除
        slotBlacklistDomainMapper.deleteByIds(ids);
        }


    private void validateSlotBlacklistDomainExists(Long id) {
        if (slotBlacklistDomainMapper.selectById(id) == null) {
            throw exception(SLOT_BLACKLIST_DOMAIN_NOT_EXISTS);
        }
    }

    @Override
    public SlotBlacklistDomainDO getSlotBlacklistDomain(Long id) {
        return slotBlacklistDomainMapper.selectById(id);
    }

    @Override
    public PageResult<SlotBlacklistDomainDO> getSlotBlacklistDomainPage(SlotBlacklistDomainPageReqVO pageReqVO) {
        return slotBlacklistDomainMapper.selectPage(pageReqVO);
    }

}