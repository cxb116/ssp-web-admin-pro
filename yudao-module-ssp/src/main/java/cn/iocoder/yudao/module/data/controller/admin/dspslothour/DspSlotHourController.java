package cn.iocoder.yudao.module.data.controller.admin.dspslothour;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import cn.iocoder.yudao.module.data.service.dspslothour.DspSlotHourService;

@Tag(name = "管理后台 - DSP预算广告位小时报")
@RestController
@RequestMapping("/data/dsp-slot-hour")
@Validated
public class DspSlotHourController {

    @Resource
    private DspSlotHourService dspSlotHourService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP预算广告位小时报")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:create')")
    public CommonResult<Long> createDspSlotHour(@Valid @RequestBody DspSlotHourSaveReqVO createReqVO) {
        return success(dspSlotHourService.createDspSlotHour(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP预算广告位小时报")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:update')")
    public CommonResult<Boolean> updateDspSlotHour(@Valid @RequestBody DspSlotHourSaveReqVO updateReqVO) {
        dspSlotHourService.updateDspSlotHour(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP预算广告位小时报")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:delete')")
    public CommonResult<Boolean> deleteDspSlotHour(@RequestParam("id") Long id) {
        dspSlotHourService.deleteDspSlotHour(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP预算广告位小时报")
//                @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:delete')")
    public CommonResult<Boolean> deleteDspSlotHourList(@RequestParam("ids") List<Long> ids) {
        dspSlotHourService.deleteDspSlotHourListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP预算广告位小时报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<DspSlotHourRespVO> getDspSlotHour(@RequestParam("id") Long id) {
        DspSlotHourDO dspSlotHour = dspSlotHourService.getDspSlotHour(id);
        return success(BeanUtils.toBean(dspSlotHour, DspSlotHourRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP预算广告位小时报分页")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<PageResult<DspSlotHourRespVO>> getDspSlotHourPage(@Valid DspSlotHourPageReqVO pageReqVO) {
        PageResult<DspSlotHourDO> pageResult = dspSlotHourService.getDspSlotHourPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotHourRespVO.class));
    }

    @GetMapping("/dsp_ssp_hour")
    @Operation(summary = "获得媒体广告位小时报子表（按 sspSlotId + date 查预算位明细）")
    @Parameter(name = "sspSlotId", description = "媒体广告位ID", required = true, example = "1024")
    @Parameter(name = "date", description = "时间 yyyyMMddHH", required = true, example = "2026071510")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<List<DspSlotHourRespVO>> getSSPDspSlotHour(
            @RequestParam("sspSlotId") Long sspSlotId,
            @RequestParam("date") Integer date) {
        List<DspSlotHourDO> list = dspSlotHourService.getDspSspSlotHour(sspSlotId, date);
        return success(BeanUtils.toBean(list, DspSlotHourRespVO.class));
    }

    @GetMapping("/trend")
    @Operation(summary = "小时报表折线图（单天0~23点按小时聚合）")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<List<DspSlotHourTrendRespVO>> getDspSlotHourTrend(@Valid DspSlotHourPageReqVO pageReqVO) {
        // 未指定日期范围时，默认查询今天（yyyyMMdd00 ~ yyyyMMdd23）
        if (pageReqVO.getDate() == null || pageReqVO.getDate().length == 0) {
            pageReqVO.setDate(buildTodayHourRange());
        }
        return success(dspSlotHourService.getDspSlotHourTrend(pageReqVO));
    }

    /**
     * 构建今天 0 点到 23 点的小时范围（yyyyMMdd00 ~ yyyyMMdd23）
     * @return 长度为 2 的日期数组
     */
    private Integer[] buildTodayHourRange() {
        java.time.LocalDate today = java.time.LocalDate.now();
        String day = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        return new Integer[]{Integer.valueOf(day + "00"), Integer.valueOf(day + "23")};
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP预算广告位小时报 Excel")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDspSlotHourExcel(@Valid DspSlotHourPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DspSlotHourDO> list = dspSlotHourService.getDspSlotHourPage(pageReqVO).getList();
        List<DspSlotHourExportVO> rows = new ArrayList<>();
        for (DspSlotHourDO item : list) {
            DspSlotHourExportVO row = BeanUtils.toBean(item, DspSlotHourExportVO.class);
            String date = item.getDate() == null ? "" : item.getDate().toString();
            row.setHour(date.length() == 10 ? date.substring(8) + ":00" : date);
            row.setFillRate(item.getFillRate() + "%");
            row.setDisplayRate(item.getDisplayRate() + "%");
            row.setClickRate(item.getClickRate() + "%");
            BigDecimal spend = item.getSpend();
            BigDecimal income = item.getIncome();
            if (spend != null || income != null) {
                row.setRevenue(toExportAmount((spend == null ? BigDecimal.ZERO : spend)
                        .add(income == null ? BigDecimal.ZERO : income)));
            }
            row.setSpend(toExportAmount(spend));
            row.setIncome(toExportAmount(income));
            row.setEcpm(toExportAmount(item.getEcpm()));
            row.setMediaEcpm(toExportAmount(item.getMediaEcpm()));
            row.setEcprm(toExportAmount(item.getEcprm()));
            row.setMediaEcprm(toExportAmount(item.getMediaEcprm()));
            rows.add(row);
        }
        // 导出 Excel
        ExcelUtils.write(response, "DSP预算广告位小时报.xls", "数据", DspSlotHourExportVO.class, rows);
    }

    private BigDecimal toExportAmount(Number value) {
        return value == null ? null : new BigDecimal(value.toString())
                .movePointLeft(5).setScale(2, RoundingMode.HALF_UP);
    }

    @GetMapping("/export-excel-detail")
    @Operation(summary = "导出DSP预算广告位小时报详情 Excel")
//    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDspSlotHourExcelDetail(@Valid DspSlotHourPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DspSlotHourRespExecVo> list = dspSlotHourService.getDspSlotHourExceVo(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP预算广告位小时报.xls", "数据", DspSlotHourRespExecVo.class, list);
    }
}