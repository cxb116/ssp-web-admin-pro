package cn.iocoder.yudao.module.dsp.service.launch;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.dsp.controller.admin.launch.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 媒体预算绑定 Service 接口
 *
 * @author 芋道源码
 */
public interface LaunchService {

    /**
     * 创建媒体预算绑定
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createLaunch(@Valid LaunchSaveReqVO createReqVO);

    /**
     * 更新媒体预算绑定
     *
     * @param updateReqVO 更新信息
     */
    void updateLaunch(@Valid LaunchSaveReqVO updateReqVO);

    /**
     * 删除媒体预算绑定
     *
     * @param id 编号
     */
    void deleteLaunch(Long id);

    /**
    * 批量删除媒体预算绑定
    *
    * @param ids 编号
    */
    void deleteLaunchListByIds(List<Long> ids);

    /**
     * 获得媒体预算绑定
     *
     * @param id 编号
     * @return 媒体预算绑定
     */
    LaunchDO getLaunch(Long id);

    /**
     * 获得媒体预算绑定分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体预算绑定分页
     */
    PageResult<LaunchDO> getLaunchPage(LaunchPageReqVO pageReqVO);

    /**
     *  根据sspSlotId去查找dspLaunch数据
     *
     * @param id 是 sspSlotId
     * @return
     */
    List<LaunchDO> getLaunchSspSlotIdQuery(Long id);


    /**
     *  根据dspSlotId去查找dspLaunch数据
     *
     * @param id 是 dspSlotId
     * @return
     */
    List<LaunchDO> getLaunchDspSlotIdQuery(Long id);

    /**
     *  SspSlotId获取DspLaunch中的SspSlotId 集合数据
     * @param id
     * @return
     */
    List<LaunchDO> getLaunchSspSlotList(Long id);
}