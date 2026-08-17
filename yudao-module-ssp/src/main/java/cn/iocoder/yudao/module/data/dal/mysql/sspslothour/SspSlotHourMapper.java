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
     * 根据预算位ID和时间查询 SSP 小时子表数据
     *
     * @param dspSlotId 预算广告位ID
     * @param date      时间(yyyyMMddHH)
     * @return 子表数据
     */
    List<SspSlotHourDO> getSspDspSlotHour(@Param("dspSlotId") Long dspSlotId, @Param("date") Long date);
}