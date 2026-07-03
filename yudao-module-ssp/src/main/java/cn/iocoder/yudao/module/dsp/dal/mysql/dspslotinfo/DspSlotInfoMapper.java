package cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.*;

/**
 * 预算广告位 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DspSlotInfoMapper extends BaseMapperX<DspSlotInfoDO> {

    /**
     * 分页查询广告位列表
     */
    List<DspSlotInfoDO> selectPage(@Param("reqVO") DspSlotInfoPageReqVO reqVO, @Param("offset") Long offset, @Param("pageSize") Integer pageSize);

    /**
     * 查询广告位总数
     */
    Long selectPageCount(@Param("reqVO") DspSlotInfoPageReqVO reqVO);

}