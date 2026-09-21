package cn.iocoder.yudao.module.data.dal.mysql.dspslothour;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
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
     * 小时报表折线图：按小时聚合（date 格式 yyyyMMddHH，MOD(date,100) 提取小时）
     *
     * @param reqVO 查询条件（包含时间范围 date[0]~date[1]）
     * @return 每小时一条聚合记录
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM + GROUP BY）会导致多租户拦截器解析失败，此处跳过租户 SQL 解析
    List<DspSlotHourDO> selectHourTrend(@Param("reqVO") DspSlotHourPageReqVO reqVO);

    /**
     * 获取SSP媒体子表小时数据
     *
     * @param sspSlotId 媒体广告位ID
     * @param date      时间(yyyyMMddHH)
     * @return 子表数据
     */
    List<DspSlotHourDO> getDspSspSlotHour(@Param("sspSlotId") Long sspSlotId, @Param("date") Integer date);

}
