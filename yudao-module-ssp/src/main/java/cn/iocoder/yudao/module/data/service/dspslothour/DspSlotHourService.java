package cn.iocoder.yudao.module.data.service.dspslothour;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP预算广告位小时报 Service 接口
 *
 * @author 芋道源码
 */
public interface DspSlotHourService {

    /**
     * 创建DSP预算广告位小时报
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDspSlotHour(@Valid DspSlotHourSaveReqVO createReqVO);

    /**
     * 更新DSP预算广告位小时报
     *
     * @param updateReqVO 更新信息
     */
    void updateDspSlotHour(@Valid DspSlotHourSaveReqVO updateReqVO);

    /**
     * 删除DSP预算广告位小时报
     *
     * @param id 编号
     */
    void deleteDspSlotHour(Long id);

    /**
    * 批量删除DSP预算广告位小时报
    *
    * @param ids 编号
    */
    void deleteDspSlotHourListByIds(List<Long> ids);

    /**
     * 获得DSP预算广告位小时报
     *
     * @param id 编号
     * @return DSP预算广告位小时报
     */
    DspSlotHourDO getDspSlotHour(Long id);

    /**
     * 获得DSP预算广告位小时报分页
     *
     * @param pageReqVO 分页查询
     * @return DSP预算广告位小时报分页
     */
    PageResult<DspSlotHourDO> getDspSlotHourPage(DspSlotHourPageReqVO pageReqVO);

    /**
     * 获得DSP预算广告位小时报导出数据（含关联字段和派生指标）
     *
     * @param pageReqVO 分页查询
     * @return DSP预算广告位小时报导出数据
     */
    PageResult<DspSlotHourRespExecVo> getDspSlotHourExceVo(DspSlotHourPageReqVO pageReqVO);

    /**
     * 获取SSP媒体子表小时数据
     *
     * @param sspSlotId 媒体广告位ID
     * @param date      时间(yyyyMMddHH)
     * @return 子表数据
     */
    List<DspSlotHourDO> getDspSspSlotHour(Long sspSlotId, Integer date);
}