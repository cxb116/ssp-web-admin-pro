package cn.iocoder.yudao.module.data.service.sspslothour;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP-SSP广告位报 Service 接口
 *
 * @author 芋道源码
 */
public interface SspSlotHourService {

    /**
     * 创建DSP-SSP广告位报
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSspSlotHour(@Valid SspSlotHourSaveReqVO createReqVO);

    /**
     * 更新DSP-SSP广告位报
     *
     * @param updateReqVO 更新信息
     */
    void updateSspSlotHour(@Valid SspSlotHourSaveReqVO updateReqVO);

    /**
     * 删除DSP-SSP广告位报
     *
     * @param id 编号
     */
    void deleteSspSlotHour(Long id);

    /**
    * 批量删除DSP-SSP广告位报
    *
    * @param ids 编号
    */
    void deleteSspSlotHourListByIds(List<Long> ids);

    /**
     * 获得DSP-SSP广告位报
     *
     * @param id 编号
     * @return DSP-SSP广告位报
     */
    SspSlotHourDO getSspSlotHour(Long id);

    /**
     * 获得DSP-SSP广告位报分页
     *
     * @param pageReqVO 分页查询
     * @return DSP-SSP广告位报分页
     */
    PageResult<SspSlotHourDO> getSspSlotHourPage(SspSlotHourPageReqVO pageReqVO);

}