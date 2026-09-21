package cn.iocoder.yudao.module.data.dal.mysql.sspslotday;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.InputIncomeReqVO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
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
     * 日报表折线图：按天聚合
     *
     * @param reqVO 查询条件（包含时间范围 date[0]~date[1]）
     * @return 每天一条聚合记录
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM + GROUP BY）会导致多租户拦截器解析失败，此处跳过租户 SQL 解析
    List<SspSlotDayDO> selectDayTrend(@Param("reqVO") SspSlotDayPageReqVO reqVO);

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

    List<SspSlotDayDO> selectMediaCompanySum(@Param("date") Long date);



    SspSlotDayDO selectDspSlotIdAndSspSlotIdAndData(@Param("sspSlotId")Long sspSlotId,@Param("dspSlotId")Long dspSlotId,@Param("date")Long date);

    int updateSspSlotDaySpendAndIncome(InputIncomeReqVO inputIncomeReqVO);

    SspSlotDayDO getDspSlotDaySum(@Param("date") List<String> date);

    SspSlotDayDO getSspSlotDaySumByCondition(@Param("reqVO") SspSlotDayPageReqVO reqVO);
}
