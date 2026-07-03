package cn.iocoder.yudao.module.dsp.service.company;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.dsp.controller.admin.company.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 预算广告 Service 接口
 *
 * @author 芋道源码
 */
public interface CompanyService {

    /**
     * 创建预算广告
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createCompany(@Valid CompanySaveReqVO createReqVO);

    /**
     * 更新预算广告
     *
     * @param updateReqVO 更新信息
     */
    void updateCompany(@Valid CompanySaveReqVO updateReqVO);

    /**
     * 删除预算广告
     *
     * @param id 编号
     */
    void deleteCompany(Long id);

    /**
    * 批量删除预算广告
    *
    * @param ids 编号
    */
    void deleteCompanyListByIds(List<Long> ids);

    /**
     * 获得预算广告
     *
     * @param id 编号
     * @return 预算广告
     */
    CompanyDO getCompany(Long id);

    /**
     * 获得预算广告分页
     *
     * @param pageReqVO 分页查询
     * @return 预算广告分页
     */
    PageResult<CompanyDO> getCompanyPage(CompanyPageReqVO pageReqVO);

}