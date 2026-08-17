package cn.iocoder.yudao.module.data.dal.mysql.sspslotday;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * DSP-SSP广告位报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SspSlotDayMapper extends BaseMapperX<SspSlotDayDO> {

    /**
     * 分页查询
     */
    Page<SspSlotDayDO> selectSspSlotDayPage(Page<SspSlotDayDO> page, @Param("reqVO") SspSlotDayPageReqVO reqVO);

    /**
     * 根据 ID 查询
     */
    SspSlotDayDO getSspSlotDay(Long id);

    /**
     * 根据 dspSlotId 查询
     */
    List<SspSlotDayDO> getDspSspSlotDay(@Param("dspSlotId") Long dspSlotId, @Param("date") Long date);

    // 查询今天的曝光数量
    SspSlotDayDO selectSspSlotId(@Param("sspSlotId") Long sspSlotId,@Param("dspSlotCode")String dspSlotCode, @Param("inputTime") String inputTime);

    void updateSspSlotDay(SspSlotDayDO sspSlotDayDO);

    SspSlotDayDO selectRequestCount(@Param("sspSlotId")Long sspSlotId,@Param("time") String time);
}