package cn.iocoder.yudao.module.ssp.service.slotblacklist;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklist.SlotBlacklistDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 黑名单配置 Service 接口
 *
 * @author 芋道源码
 */
public interface SlotBlacklistService {

    /**
     * 创建黑名单配置
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSlotBlacklist(@Valid SlotBlacklistSaveReqVO createReqVO);

    /**
     * 更新黑名单配置
     *
     * @param updateReqVO 更新信息
     */
    void updateSlotBlacklist(@Valid SlotBlacklistSaveReqVO updateReqVO);

    /**
     * 删除黑名单配置
     *
     * @param id 编号
     */
    void deleteSlotBlacklist(Long id);

    /**
    * 批量删除黑名单配置
    *
    * @param ids 编号
    */
    void deleteSlotBlacklistListByIds(List<Long> ids);

    /**
     * 获得黑名单配置
     *
     * @param id 编号
     * @return 黑名单配置
     */
    SlotBlacklistDO getSlotBlacklist(Long id);

    /**
     * 获得黑名单配置分页
     *
     * @param pageReqVO 分页查询
     * @return 黑名单配置分页
     */
    PageResult<SlotBlacklistDO> getSlotBlacklistPage(SlotBlacklistPageReqVO pageReqVO);

}