package cn.iocoder.yudao.module.data.service.dspslotday;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.SspSlotDayPageReqVO;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;

/**
 * DSP预算广告位日期报 Service 接口
 *
 * @author 芋道源码
 */
public interface DspSlotDayService {

    /**
     * 创建DSP预算广告位日期报
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createDspSlotDay(@Valid DspSlotDaySaveReqVO createReqVO);

    /**
     * 更新DSP预算广告位日期报
     *
     * @param updateReqVO 更新信息
     */
    void updateDspSlotDay(@Valid DspSlotDaySaveReqVO updateReqVO);

    /**
     * 删除DSP预算广告位日期报
     *
     * @param id 编号
     */
    void deleteDspSlotDay(Long id);

    /**
    * 批量删除DSP预算广告位日期报
    *
    * @param ids 编号
    */
    void deleteDspSlotDayListByIds(List<Long> ids);

    /**
     * 获得DSP预算广告位日期报
     *
     * @param id 编号
     * @return DSP预算广告位日期报
     */
    DspSlotDayDO getDspSlotDay(Long id);

    /**
     * 获得DSP预算广告位日期报分页
     *
     * @param pageReqVO 分页查询
     * @return DSP预算广告位日期报分页
     */
    PageResult<DspSlotDayDO> getDspSlotDayPage(DspSlotDayPageReqVO pageReqVO);



    PageResult<DspSlotDayRespExecVo> getDspSlotDayExceVo(DspSlotDayPageReqVO pageReqVO);
    /**
     *  获取SSP媒体子表天表数据
     * @param sspSlotId
     * @return
     */
    List<DspSlotDayDO> getSSPDspSlotDay(Long sspSlotId, int date);

    /**
     * 合计
     * @param date
     * @return
     */
    DspSlotDayRespVO getDspSlotDaySum(Long date);

    /**
     * 预算详情信息
     * @param pageReqVO
     * @return
     */
    PageResult<DspSlotDayDO> getDspSlotDayPageDetail(@Valid DspSlotDayPageReqVO pageReqVO);

//    PageResult<DspSlotDayRespExecVo> getDspSlotDayDeatil(@Valid SspSlotDayPageReqVO pageReqVO);
}