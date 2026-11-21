package cn.iocoder.yudao.module.ssp.service.slotblacklistdomain;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklistdomain.SlotBlacklistDomainDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 黑名单统计 Service 接口
 *
 * @author 芋道源码
 */
public interface SlotBlacklistDomainService {

    /**
     * 创建黑名单统计
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSlotBlacklistDomain(@Valid SlotBlacklistDomainSaveReqVO createReqVO);

    /**
     * 更新黑名单统计
     *
     * @param updateReqVO 更新信息
     */
    void updateSlotBlacklistDomain(@Valid SlotBlacklistDomainSaveReqVO updateReqVO);

    /**
     * 删除黑名单统计
     *
     * @param id 编号
     */
    void deleteSlotBlacklistDomain(Long id);

    /**
    * 批量删除黑名单统计
    *
    * @param ids 编号
    */
    void deleteSlotBlacklistDomainListByIds(List<Long> ids);

    /**
     * 获得黑名单统计
     *
     * @param id 编号
     * @return 黑名单统计
     */
    SlotBlacklistDomainDO getSlotBlacklistDomain(Long id);

    /**
     * 获得黑名单统计分页
     *
     * @param pageReqVO 分页查询
     * @return 黑名单统计分页
     */
    PageResult<SlotBlacklistDomainDO> getSlotBlacklistDomainPage(SlotBlacklistDomainPageReqVO pageReqVO);

}