package cn.iocoder.yudao.module.domain.service.domain;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.domain.controller.admin.domain.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domain.DomainDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.domain.dal.mysql.domain.DomainMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DOMAIN_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP域名管理 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DomainServiceImpl implements DomainService {

    @Resource
    private DomainMapper domainMapper;

    @Override
    public Long createDomain(DomainSaveReqVO createReqVO) {
        // 插入
        DomainDO domain = BeanUtils.toBean(createReqVO, DomainDO.class);
        domainMapper.insert(domain);

        // 返回
        return domain.getId();
    }

    @Override
    public void updateDomain(DomainSaveReqVO updateReqVO) {
        // 校验存在
        validateDomainExists(updateReqVO.getId());
        // 更新
        DomainDO updateObj = BeanUtils.toBean(updateReqVO, DomainDO.class);
        domainMapper.updateById(updateObj);
    }

    @Override
    public void deleteDomain(Long id) {
        // 校验存在
        validateDomainExists(id);
        // 删除
        domainMapper.deleteById(id);
    }

    @Override
        public void deleteDomainListByIds(List<Long> ids) {
        // 删除
        domainMapper.deleteByIds(ids);
        }


    private void validateDomainExists(Long id) {
        if (domainMapper.selectById(id) == null) {
            throw exception(DOMAIN_NOT_EXISTS);
        }
    }

    @Override
    public DomainDO getDomain(Long id) {
        return domainMapper.selectById(id);
    }

    @Override
    public PageResult<DomainDO> getDomainPage(DomainPageReqVO pageReqVO) {
        return domainMapper.selectPage(pageReqVO);
    }

}