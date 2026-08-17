package cn.iocoder.yudao.module.data.dal.mysql.dspslothour;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * DSP预算广告位小时报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DspSlotHourMapper extends BaseMapperX<DspSlotHourDO> {

    /**
     * 分页查询（XML 多表关联）
     */
    Page<DspSlotHourDO> selectDspSlotHourPage(Page<DspSlotHourDO> page, @Param("reqVO") DspSlotHourPageReqVO reqVO, @Param("sortField") String sortField);

    /**
     * 获取SSP媒体子表小时数据
     *
     * @param sspSlotId 媒体广告位ID
     * @param date      时间(yyyyMMddHH)
     * @return 子表数据
     */
    List<DspSlotHourDO> getDspSspSlotHour(@Param("sspSlotId") Long sspSlotId, @Param("date") Integer date);

}