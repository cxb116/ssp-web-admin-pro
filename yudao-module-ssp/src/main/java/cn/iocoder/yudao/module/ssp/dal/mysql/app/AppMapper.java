package cn.iocoder.yudao.module.ssp.dal.mysql.app;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ssp.controller.admin.app.vo.AppPageReqVO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 媒体应用 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface AppMapper extends BaseMapperX<AppDO> {

    /**
     * 根据 ID 查询应用详情
     */
    AppDO selectAppById(Long id);

    /**
     * 查询应用列表（用于分页）
     */
    List<AppDO> selectAppPage(@Param("reqVO") AppPageReqVO reqVO, @Param("offset") Long offset, @Param("pageSize") Integer pageSize);

    /**
     * 查询应用总数
     */
    Long selectAppPageCount(@Param("reqVO") AppPageReqVO reqVO);

    /**
     * 在应用管理中下拉用媒体id,去查应用
     * @param id
     * @return
     */

    List<AppDO> getAppMediaList(Long id);
}