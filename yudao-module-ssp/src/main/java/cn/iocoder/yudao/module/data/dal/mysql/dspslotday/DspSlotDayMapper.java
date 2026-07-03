package cn.iocoder.yudao.module.data.dal.mysql.dspslotday;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * DSP预算广告位日期报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DspSlotDayMapper extends BaseMapperX<DspSlotDayDO> {

    /**
     * 分页查询
     */
    Page<DspSlotDayDO> selectDspSlotDayPage(Page<DspSlotDayDO> page, @Param("reqVO") DspSlotDayPageReqVO reqVO);

    /**
     * 根据 ID 查询
     */
    DspSlotDayDO getDspSlotDay(Long id);

    /**
     *  获取SSP媒体子表天表数据
     * @param sspSlotId
     * @return
     */
    List<DspSlotDayDO> getSSPDspSlotDay(Long sspSlotId, int date);
}