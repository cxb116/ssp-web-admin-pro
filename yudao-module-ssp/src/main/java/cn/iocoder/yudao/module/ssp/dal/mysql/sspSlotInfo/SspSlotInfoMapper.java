package cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo.*;

/**
 * 媒体广告位 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SspSlotInfoMapper extends BaseMapperX<SspSlotInfoDO> {

    /**
     * 分页查询广告位列表
     */
    List<SspSlotInfoDO> selectPage(@Param("reqVO") SspSlotInfoPageReqVO reqVO, @Param("offset") Long offset, @Param("pageSize") Integer pageSize);

    /**
     * 查询广告位总数
     */
    Long selectPageCount(@Param("reqVO") SspSlotInfoPageReqVO reqVO);

    /**
     * 根据 ID 查询广告位详情（带关联数据）
     */
    SspSlotInfoDO selectSlotInfoById(Long id);

}