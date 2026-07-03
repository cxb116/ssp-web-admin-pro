package cn.iocoder.yudao.module.ssp.service.app;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.ssp.controller.admin.app.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 媒体应用 Service 接口
 *
 * @author 芋道源码
 */
public interface AppService {

    /**
     * 创建媒体应用
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createApp(@Valid AppSaveReqVO createReqVO);

    /**
     * 更新媒体应用
     *
     * @param updateReqVO 更新信息
     */
    void updateApp(@Valid AppSaveReqVO updateReqVO);

    /**
     * 删除媒体应用
     *
     * @param id 编号
     */
    void deleteApp(Long id);

    /**
    * 批量删除媒体应用
    *
    * @param ids 编号
    */
    void deleteAppListByIds(List<Long> ids);

    /**
     * 获得媒体应用
     *
     * @param id 编号
     * @return 媒体应用
     */
    AppDO getApp(Long id);

    /**
     * 获得媒体应用分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体应用分页
     */
    PageResult<AppDO> getAppPage(AppPageReqVO pageReqVO);

    /**
     * 获得媒体关联应用
     *
     * @param  id media查询
     * @return 媒体应用
     */

    List<AppDO> getAppMediaList(Long id);
}