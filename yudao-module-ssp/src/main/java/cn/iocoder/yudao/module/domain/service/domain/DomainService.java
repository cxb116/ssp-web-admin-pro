package cn.iocoder.yudao.module.domain.service.domain;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.domain.controller.admin.domain.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domain.DomainDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP域名管理 Service 接口
 *
 * @author 芋道源码
 */
public interface DomainService {

    /**
     * 创建DSP域名管理
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDomain(@Valid DomainSaveReqVO createReqVO);

    /**
     * 更新DSP域名管理
     *
     * @param updateReqVO 更新信息
     */
    void updateDomain(@Valid DomainSaveReqVO updateReqVO);

    /**
     * 删除DSP域名管理
     *
     * @param id 编号
     */
    void deleteDomain(Long id);

    /**
    * 批量删除DSP域名管理
    *
    * @param ids 编号
    */
    void deleteDomainListByIds(List<Long> ids);

    /**
     * 获得DSP域名管理
     *
     * @param id 编号
     * @return DSP域名管理
     */
    DomainDO getDomain(Long id);

    /**
     * 获得DSP域名管理分页
     *
     * @param pageReqVO 分页查询
     * @return DSP域名管理分页
     */
    PageResult<DomainDO> getDomainPage(DomainPageReqVO pageReqVO);

}