package cn.iocoder.yudao.module.domain.service.domaincompany;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaincompany.DomainCompanyDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.domain.dal.mysql.domaincompany.DomainCompanyMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DOMAIN_COMPANY_DOMAIN_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DOMAIN_NOT_COMPANY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP域名公司列 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DomainCompanyServiceImpl implements DomainCompanyService {

    @Resource
    private DomainCompanyMapper domainCompanyMapper;

    @Override
    public Long createCompany(DomainCompanySaveReqVO createReqVO) {
        // 域名已存在时不允许重复添加
        validateDomainNameUnique(null, createReqVO.getDomainName());
        // 插入
        DomainCompanyDO company = BeanUtils.toBean(createReqVO, DomainCompanyDO.class);
        domainCompanyMapper.insert(company);

        // 返回
        return company.getId();
    }

    @Override
    public void updateCompany(DomainCompanySaveReqVO updateReqVO) {
        // 校验存在
        validateCompanyExists(updateReqVO.getId());
        // 域名不能改成其他已有记录的域名
        validateDomainNameUnique(updateReqVO.getId(), updateReqVO.getDomainName());
        // 更新
        DomainCompanyDO updateObj = BeanUtils.toBean(updateReqVO, DomainCompanyDO.class);
        domainCompanyMapper.updateById(updateObj);
    }

    @Override
    public void deleteCompany(Long id) {
        // 校验存在
        validateCompanyExists(id);
        // 删除
        domainCompanyMapper.deleteById(id);
    }

    @Override
        public void deleteCompanyListByIds(List<Long> ids) {
        // 删除
        domainCompanyMapper.deleteByIds(ids);
        }


    private void validateCompanyExists(Long id) {
        if (domainCompanyMapper.selectById(id) == null) {
            throw exception(DOMAIN_NOT_COMPANY_NOT_EXISTS);
        }
    }

    /**
     * 校验域名在 dsp_domain_company 中是否已存在（已存在则抛错）
     *
     * @param id         当前记录 ID，更新时排除自身；新增传 null
     * @param domainName 域名；表排序规则为 *_ci，大小写不同也视为重复
     */
    private void validateDomainNameUnique(Long id, String domainName) {
        if (domainName == null || domainName.isEmpty()) {
            return;
        }
        // 历史数据可能已有重复，用 selectList 而非 selectOne，避免查出多条直接报错
        for (DomainCompanyDO existing : domainCompanyMapper.selectList(DomainCompanyDO::getDomainName, domainName)) {
            if (!existing.getId().equals(id)) {
                throw exception(DOMAIN_COMPANY_DOMAIN_EXISTS);
            }
        }
    }

    @Override
    public DomainCompanyDO getCompany(Long id) {
        return domainCompanyMapper.selectById(id);
    }

    @Override
    public PageResult<DomainCompanyDO> getCompanyPage(DomainCompanyPageReqVO pageReqVO) {
        return domainCompanyMapper.selectPage(pageReqVO);
    }

}