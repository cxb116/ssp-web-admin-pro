package cn.iocoder.yudao.module.data.service.sspslotday;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP-SSP广告位报 Service 接口
 *
 * @author 芋道源码
 */
public interface SspSlotDayService {

    /**
     * 创建DSP-SSP广告位报
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSspSlotDay(@Valid SspSlotDaySaveReqVO createReqVO);

    /**
     * 更新DSP-SSP广告位报
     *
     * @param updateReqVO 更新信息
     */
    void updateSspSlotDay(@Valid SspSlotDaySaveReqVO updateReqVO);

    /**
     * 删除DSP-SSP广告位报
     *
     * @param id 编号
     */
    void deleteSspSlotDay(Long id);

    /**
    * 批量删除DSP-SSP广告位报
    *
    * @param ids 编号
    */
    void deleteSspSlotDayListByIds(List<Long> ids);

    /**
     * 获得DSP-SSP广告位报
     *
     * @param id 编号
     * @return DSP-SSP广告位报
     */
    SspSlotDayDO getSspSlotDay(Long id);

    /**
     * 获得DSP-SSP广告位报分页
     *
     * @param pageReqVO 分页查询
     * @return DSP-SSP广告位报分页
     */
    PageResult<SspSlotDayDO> getSspSlotDayPage(SspSlotDayPageReqVO pageReqVO);

    List<SspSlotDayDO> getDspSspSlotDay(Long dspSlotId,Long date);

    /**
     * 合计
     * @param date 日期
     * @return 聚合结果
     */
    SspSlotDayRespVO getSspSlotDaySum(Long date);
}