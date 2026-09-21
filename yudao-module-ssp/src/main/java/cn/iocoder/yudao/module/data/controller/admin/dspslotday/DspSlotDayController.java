package cn.iocoder.yudao.module.data.controller.admin.dspslotday;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.*;
import javax.servlet.http.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.service.dspslotday.DspSlotDayService;

@Tag(name = "管理后台 - DSP预算广告位日期报")
@RestController
@RequestMapping("/data/dsp-slot-day")
@Validated
public class DspSlotDayController {

    @Resource
    private DspSlotDayService dspSlotDayService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP预算广告位日期报")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:create')")
    public CommonResult<Long> createDspSlotDay(@Valid @RequestBody DspSlotDaySaveReqVO createReqVO) {
        return success(dspSlotDayService.createDspSlotDay(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP预算广告位日期报")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:update')")
    public CommonResult<Boolean> updateDspSlotDay(@Valid @RequestBody DspSlotDaySaveReqVO updateReqVO) {
        dspSlotDayService.updateDspSlotDay(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP预算广告位日期报")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:delete')")
    public CommonResult<Boolean> deleteDspSlotDay(@RequestParam("id") Long id) {
        dspSlotDayService.deleteDspSlotDay(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP预算广告位日期报")
                @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:delete')")
    public CommonResult<Boolean> deleteDspSlotDayList(@RequestParam("ids") List<Long> ids) {
        dspSlotDayService.deleteDspSlotDayListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP预算广告位日期报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<DspSlotDayRespVO> getDspSlotDay(@RequestParam("id") Long id) {
        DspSlotDayDO dspSlotDay = dspSlotDayService.getDspSlotDay(id);
        return success(BeanUtils.toBean(dspSlotDay, DspSlotDayRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP预算广告位日期报分页")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<PageResult<DspSlotDayRespVO>> getDspSlotDayPage(@Valid DspSlotDayPageReqVO pageReqVO) {
        PageResult<DspSlotDayDO> pageResult = dspSlotDayService.getDspSlotDayPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotDayRespVO.class));
    }


    @GetMapping("/page-detail")
    @Operation(summary = "获得DSP预算广告位日期报分页")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<PageResult<DspSlotDayRespVO>> getDspSlotDayPageDetail(@Valid DspSlotDayPageReqVO pageReqVO) {
        PageResult<DspSlotDayDO> pageResult = dspSlotDayService.getDspSlotDayPageDetail(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotDayRespVO.class));
    }


    @GetMapping("/sum")
    @Operation(summary = "合计")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<DspSlotDayRespVO> getDspSlotDaySum(@Valid DspSlotDayPageReqVO reqVO) {
        String[] date = reqVO.getDate() == null ? null : reqVO.getDate().toArray(new String[0]);
        if (date != null && date.length == 1 && date[0] != null && date[0].contains(",")) {
            date = date[0].split(",");
        }
        if (date == null || date.length != 2 || date[0] == null || date[1] == null) {
            throw new IllegalArgumentException("日期格式不正确，应为[开始日期,结束日期]，格式yyyyMMdd");
        }
        List<String> normalizedDate = Arrays.asList(
                date[0].trim().replace("-", ""),
                date[1].trim().replace("-", ""));
        if (!normalizedDate.get(0).matches("\\d{8}") || !normalizedDate.get(1).matches("\\d{8}")
                || normalizedDate.get(0).compareTo(normalizedDate.get(1)) > 0) {
            throw new IllegalArgumentException("日期格式不正确，应为[开始日期,结束日期]，格式yyyyMMdd");
        }
        reqVO.setDate(normalizedDate);
        DspSlotDayRespVO result = dspSlotDayService.getDspSlotDaySum(reqVO);
        return success(result);
    }

    @GetMapping("/trend")
    @Operation(summary = "日报表折线图（按天聚合）")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<List<DspSlotDayTrendRespVO>> getDspSlotDayTrend(@Valid DspSlotDayPageReqVO pageReqVO) {
        // 时间范围切换：date 数组长度 >= 2 表示存在起止范围
        if (pageReqVO.getDate() != null && pageReqVO.getDate().size() >= 2) {
            return success(dspSlotDayService.getDspSlotDayTrend(pageReqVO));
        }
        // 未传时间范围时，默认返回最近 7 天数据
        DspSlotDayPageReqVO defaultReqVO = buildDefaultWeekReqVO(pageReqVO);
        return success(dspSlotDayService.getDspSlotDayTrend(defaultReqVO));
    }

    /**
     * 当未指定时间范围时，默认取最近 7 天（含今天）
     * @param reqVO 原始查询条件（不含时间范围）
     * @return 补全默认时间范围的查询条件
     */
    private DspSlotDayPageReqVO buildDefaultWeekReqVO(DspSlotDayPageReqVO reqVO) {
        // 计算最近 7 天的起止日期（yyyy-MM-dd）
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate start = today.minusDays(6);
        String startStr = start.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        String endStr = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        reqVO.setDate(Arrays.asList(startStr, endStr));
        return reqVO;
    }

//    @GetMapping("/details")
//    @Operation(summary = "获取预算详细列表")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
//    public CommonResult<PageResult<DspSlotDayRespExecVo>> getDspSlotDayDeatil(@Valid SspSlotDayPageReqVO pageReqVO) {
//        PageResult<DspSlotDayRespExecVo> pageResult = dspSlotDayService.getDspSlotDayDeatil(pageReqVO);
//        return success(BeanUtils.toBean(pageResult, DspSlotDayRespExecVo.class));
//    }


    @GetMapping("/dsp_ssp_day")
    @Operation(summary = "获得SSP子表天表数据广告位报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<List<DspSlotDayRespVO>> getSSPDspSlotDay(Long sspSlotId, int date) {
        List<DspSlotDayDO> dspSlotDay = dspSlotDayService.getSSPDspSlotDay(sspSlotId, date);
        return success(BeanUtils.toBean(dspSlotDay, DspSlotDayRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP预算广告位日期报 Excel")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDspSlotDayExcel(@Valid DspSlotDayPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DspSlotDayDO> list = dspSlotDayService.getDspSlotDayPage(pageReqVO).getList();

        for (DspSlotDayDO dspSlotDayDO : list) {
            BigDecimal divisor = BigDecimal.valueOf(100000);

            BigDecimal income = dspSlotDayDO.getIncome();
            BigDecimal spend = dspSlotDayDO.getSpend();

            dspSlotDayDO.setTotalIncome(
                    income.add(spend).divide(divisor, 2, RoundingMode.HALF_UP)
            );

            dspSlotDayDO.setSpend(
                    spend.divide(divisor, 2, RoundingMode.HALF_UP)
            );

            dspSlotDayDO.setIncome(
                    income.divide(divisor, 2, RoundingMode.HALF_UP)
            );
        }

        // 导出 Excel
        ExcelUtils.write(response, "预算广告位日期报.xls", "数据", DspSlotDayRespVO.class,
                        BeanUtils.toBean(list, DspSlotDayRespVO.class));
    }


    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出DSP预算广告位日期报 Excel")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDspSlotDayExcelDateil(@Valid DspSlotDayPageReqVO pageReqVO,
                                      HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        // 使用 DspSlotDayRespExecVo 导出，包含 JOIN 字段与派生指标
        List<DspSlotDayRespExecVo> list = dspSlotDayService.getDspSlotDayExceVo(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP预算广告位日期报.xls", "数据", DspSlotDayRespExecVo.class, list);
    }


    @GetMapping("/dsp-list")
    @Operation(summary = "获得DSP预算")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<List<DspSlotDayRespVO>> getDspList(@RequestParam("date") Long date) {
        return success(BeanUtils.toBean(dspSlotDayService.getDspCompanySum(date), DspSlotDayRespVO.class));
    }


    @GetMapping("/page-info")
    @Operation(summary = "预算详情数据表")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-day:query')")
    public CommonResult<PageResult<DspSlotDayPageInfoRespVO>> getDspSlotDayPageInfo(@Valid DspSlotDayPageReqVO pageReqVO) {
        PageResult<DspSlotDayDO> pageResult = dspSlotDayService.getDspSlotDayPageInfo(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotDayPageInfoRespVO.class));
    }

}
