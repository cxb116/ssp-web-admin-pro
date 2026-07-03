package cn.iocoder.yudao.module.ssp.service.media;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.ssp.controller.admin.media.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.media.MediaDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 媒体 Service 接口
 *
 * @author 少年阿宾
 */
public interface MediaService {

    /**
     * 创建媒体
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createMedia(@Valid MediaSaveReqVO createReqVO);

    /**
     * 更新媒体
     *
     * @param updateReqVO 更新信息
     */
    void updateMedia(@Valid MediaSaveReqVO updateReqVO);

    /**
     * 删除媒体
     *
     * @param id 编号
     */
    void deleteMedia(Long id);

    /**
    * 批量删除媒体
    *
    * @param ids 编号
    */
    void deleteMediaListByIds(List<Long> ids);

    /**
     * 获得媒体
     *
     * @param id 编号
     * @return 媒体
     */
    MediaDO getMedia(Long id);

    /**
     * 获得媒体分页
     *
     * @param pageReqVO 分页查询
     * @return 媒体分页
     */
    PageResult<MediaDO> getMediaPage(MediaPageReqVO pageReqVO);

}