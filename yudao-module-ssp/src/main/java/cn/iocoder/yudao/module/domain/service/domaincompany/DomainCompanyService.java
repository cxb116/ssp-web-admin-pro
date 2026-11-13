package cn.iocoder.yudao.module.domain.service.domaincompany;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaincompany.DomainCompanyDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP域名公司列 Service 接口
 *
 * @author 芋道源码
 */
public interface DomainCompanyService {

    /**
     * 创建DSP域名公司列
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCompany(@Valid DomainCompanySaveReqVO createReqVO);

    /**
     * 更新DSP域名公司列
     *
     * @param updateReqVO 更新信息
     */
    void updateCompany(@Valid DomainCompanySaveReqVO updateReqVO);

    /**
     * 删除DSP域名公司列
     *
     * @param id 编号
     */
    void deleteCompany(Long id);

    /**
    * 批量删除DSP域名公司列
    *
    * @param ids 编号
    */
    void deleteCompanyListByIds(List<Long> ids);

    /**
     * 获得DSP域名公司列
     *
     * @param id 编号
     * @return DSP域名公司列
     */
    DomainCompanyDO getCompany(Long id);

    /**
     * 获得DSP域名公司列分页
     *
     * @param pageReqVO 分页查询
     * @return DSP域名公司列分页
     */
    PageResult<DomainCompanyDO> getCompanyPage(DomainCompanyPageReqVO pageReqVO);

}