package cn.iocoder.yudao.module.dsp.service.dspslotinfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 预算广告位 Service 接口
 *
 * @author 芋道源码
 */
public interface DspSlotInfoService {

    /**
     * 创建预算广告位
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSlotInfo(@Valid DspSlotInfoSaveReqVO createReqVO);

    /**
     * 更新预算广告位
     *
     * @param updateReqVO 更新信息
     */
    void updateSlotInfo(@Valid DspSlotInfoSaveReqVO updateReqVO);

    /**
     * 删除预算广告位
     *
     * @param id 编号
     */
    void deleteSlotInfo(Long id);

    /**
    * 批量删除预算广告位
    *
    * @param ids 编号
    */
    void deleteSlotInfoListByIds(List<Long> ids);

    /**
     * 获得预算广告位
     *
     * @param id 编号
     * @return 预算广告位
     */
    DspSlotInfoDO getSlotInfo(Long id);

    /**
     * 获得预算广告位分页
     *
     * @param pageReqVO 分页查询
     * @return 预算广告位分页
     */
    PageResult<DspSlotInfoDO> getSlotInfoPage(DspSlotInfoPageReqVO pageReqVO);


}