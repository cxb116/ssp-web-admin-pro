package cn.iocoder.yudao.module.data.dal.mysql.dspslotday;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.DspSlotDayInputRespVo;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.InputIncomeReqVO;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.SspSlotDayPageReqVO;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import org.apache.ibatis.annotations.Param;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
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
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM/MAX + GROUP BY）会导致多租户拦截器的 JSQLParser 解析失败，此处跳过租户 SQL 解析
    Page<DspSlotDayDO> selectDspSlotDayPage(Page<DspSlotDayDO> page, @Param("reqVO") DspSlotDayPageReqVO reqVO, @Param("sortField") String sortField);

    /**
     * 日报表折线图：按天聚合
     *
     * @param reqVO 查询条件（包含时间范围 date[0]~date[1]）
     * @return 每天一条聚合记录
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（SUM + GROUP BY）会导致多租户拦截器解析失败，此处跳过租户 SQL 解析
    List<DspSlotDayDO> selectDayTrend(@Param("reqVO") DspSlotDayPageReqVO reqVO);

    List<DspSlotDayDO> selectDspCompanySum(@Param("date") Long date);

    /**
     * 根据 ID 查询
     */
    DspSlotDayDO getDspSlotDay(Long id);

    /**
     *  获取SSP媒体子表天表数据
     * @param sspSlotId
     * @return
     */
    List<DspSlotDayDO> getSSPDspSlotDay(@Param("sspSlotId") Long sspSlotId, @Param("date") int date);

    /**
     * 根据 DSP 广告位编码和日期查询日报数据。
     */
    List<DspSlotDayDO> selectDspSlotDayDspSlotCodeAndTInputTime(@Param("dspSlotCode") String dspSlotCode,
                                                                 @Param("inputTime") String inputTime);

    void updateSsspSlotDay(DspSlotDayDO dspSlotDay);

    PageResult<DspSlotDayRespExecVo> getDspSlotDayDeatil(SspSlotDayPageReqVO pageReqVO);

    PageResult<DspSlotDayDO> getDspSlotDayPageDetail(DspSlotDayPageReqVO pageReqVO);


    DspSlotDayInputRespVo selectDspSlotDayAllSpend(@Param("dspSlotCode") String dspSlotCode,
                                                    @Param("dspSlotId") Long dspSlotId,
                                                    @Param("sspSlotId") Long sspSlotId,
                                                    @Param("inputTime") String inputTimeResult);

    int updateDspSlotDaySpendAndIncome(InputIncomeReqVO inputIncomeReqVO);

    // 预算数据总和
    DspSlotDayDO getDspSlotDaySum(@Param("date") List<String> date);
    DspSlotDayDO getDspSlotDaySumByCondition(@Param("reqVO") DspSlotDayPageReqVO reqVO);

}
