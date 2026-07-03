package cn.iocoder.yudao.module.ssp.service.sspSlotInfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 媒体广告位 Service 接口
 *
 * @author 芋道源码
 */
public interface SspSlotInfoService {

    /**
     * 创建媒体广告位
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createSlotInfo(@Valid SspSlotInfoSaveReqVO createReqVO);

    /**
     * 更新媒体广告位
     *
     * @param updateReqVO 更新信息
     */
    void updateSlotInfo(@Valid SspSlotInfoUpdateReqVO updateReqVO);

    /**
     * 删除媒体广告位
     *
     * @param id 编号
     */
    void deleteSlotInfo(Long id);

    /**
    * 批量删除媒体广告位
    *
    * @param ids 编号
    */
    void deleteSlotInfoListByIds(List<Long> ids);

    /**
     * 获得媒体广告位
     *
     * @param id 编号
     * @return 媒体广告位
     */
    SspSlotInfoDO getSlotInfo(Long id);

    /**
     * 获得媒体广告位分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体广告位分页
     */
    PageResult<SspSlotInfoDO> getSlotInfoPage(SspSlotInfoPageReqVO pageReqVO);

}