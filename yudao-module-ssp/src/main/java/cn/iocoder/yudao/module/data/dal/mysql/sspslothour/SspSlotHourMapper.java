package cn.iocoder.yudao.module.data.dal.mysql.sspslothour;

import java.util.*;

import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * DSP-SSP广告位报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SspSlotHourMapper extends BaseMapperX<SspSlotHourDO> {

    /**
     * 分页查询
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM/MAX + GROUP BY）会导致多租户拦截器的 JSQLParser 解析失败，此处跳过租户 SQL 解析
    Page<SspSlotHourDO> selectSspSlotHourPage(Page<SspSlotHourDO> page, @Param("reqVO") SspSlotHourPageReqVO reqVO, @Param("sortField") String sortField);

    /**
     * 小时报表折线图：按小时聚合（date 格式 yyyyMMddHH，单天范围由 reqVO.date[0]~date[1] 界定）
     *
     * @param reqVO 查询条件
     * @return 每小一条聚合记录（date 字段存放小时值 0~23）
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM + GROUP BY）会导致多租户拦截器解析失败，此处跳过租户 SQL 解析
    List<SspSlotHourDO> selectHourTrend(@Param("reqVO") SspSlotHourPageReqVO reqVO);

    /**
     * 根据预算位ID和时间查询 SSP 小时子表数据
     *
     * @param dspSlotId 预算广告位ID
     * @param date      时间(yyyyMMddHH)
     * @return 子表数据
     */
    List<SspSlotHourDO> getSspDspSlotHour(@Param("dspSlotId") Long dspSlotId, @Param("date") Long date);
}