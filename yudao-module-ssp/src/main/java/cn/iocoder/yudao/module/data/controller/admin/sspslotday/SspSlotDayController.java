package cn.iocoder.yudao.module.data.controller.admin.sspslotday;

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

import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.module.data.service.sspslotday.SspSlotDayService;

@Tag(name = "管理后台 - DSP-SSP广告位报")
@RestController
@RequestMapping("/data/ssp-slot-day")
@Validated
public class SspSlotDayController {

    @Resource
    private SspSlotDayService sspSlotDayService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP-SSP广告位报")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:create')")
    public CommonResult<Long> createSspSlotDay(@Valid @RequestBody SspSlotDaySaveReqVO createReqVO) {
        return success(sspSlotDayService.createSspSlotDay(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP-SSP广告位报")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:update')")
    public CommonResult<Boolean> updateSspSlotDay(@Valid @RequestBody SspSlotDaySaveReqVO updateReqVO) {
        sspSlotDayService.updateSspSlotDay(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP-SSP广告位报")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:delete')")
    public CommonResult<Boolean> deleteSspSlotDay(@RequestParam("id") Long id) {
        sspSlotDayService.deleteSspSlotDay(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP-SSP广告位报")
                @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:delete')")
    public CommonResult<Boolean> deleteSspSlotDayList(@RequestParam("ids") List<Long> ids) {
        sspSlotDayService.deleteSspSlotDayListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP-SSP广告位报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<SspSlotDayRespVO> getSspSlotDay(@RequestParam("id") Long id) {
        SspSlotDayDO sspSlotDay = sspSlotDayService.getSspSlotDay(id);
        return success(BeanUtils.toBean(sspSlotDay, SspSlotDayRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP-SSP广告位报分页")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<PageResult<SspSlotDayRespVO>> getSspSlotDayPage(@Valid SspSlotDayPageReqVO pageReqVO) {
        PageResult<SspSlotDayDO> pageResult = sspSlotDayService.getSspSlotDayPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SspSlotDayRespVO.class));
    }

    @GetMapping("/dsp_ssp_day")
    @Operation(summary = "获得SSP子表DSP表数据广告位报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<List<SspSlotDayRespVO>> getDspSspSlotDay(Long dspSlotId, Long date) {
       List<SspSlotDayDO> sspSlotDay = sspSlotDayService.getDspSspSlotDay(dspSlotId, date);
       return success(BeanUtils.toBean(sspSlotDay, SspSlotDayRespVO.class));
    }

    @GetMapping("/sum")
    @Operation(summary = "合计")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<SspSlotDayRespVO> getSspSlotDaySum(@Valid SspSlotDayPageReqVO reqVO) {
        String[] date = reqVO.getDate();
        if (date != null && date.length == 1 && date[0] != null && date[0].contains(",")) date = date[0].split(",");
        if (date == null || date.length != 2 || date[0] == null || date[1] == null) {
            throw new IllegalArgumentException("日期格式不正确，应为[开始日期,结束日期]，格式yyyyMMdd");
        }
        List<String> normalizedDate = Arrays.asList(date[0].trim().replace("-", ""), date[1].trim().replace("-", ""));
        if (!normalizedDate.get(0).matches("\\d{8}") || !normalizedDate.get(1).matches("\\d{8}")
                || normalizedDate.get(0).compareTo(normalizedDate.get(1)) > 0) {
            throw new IllegalArgumentException("日期格式不正确，应为[开始日期,结束日期]，格式yyyyMMdd");
        }
        reqVO.setDate(normalizedDate.toArray(new String[0]));
        SspSlotDayRespVO result = sspSlotDayService.getSspSlotDaySum(reqVO);
        return success(result);
    }

    @GetMapping("/media-list")
    @Operation(summary = "按媒体公司汇总指定日期数据")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<List<SspSlotDayRespVO>> getMediaList(@RequestParam("date") Long date) {
        return success(BeanUtils.toBean(sspSlotDayService.getMediaCompanySum(date), SspSlotDayRespVO.class));
    }

    @GetMapping("/trend")
    @Operation(summary = "日报表折线图（按天聚合）")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:query')")
    public CommonResult<List<SspSlotDayTrendRespVO>> getSspSlotDayTrend(@Valid SspSlotDayPageReqVO pageReqVO) {
        // 时间范围切换：date 数组长度 >= 2 表示存在起止范围
        if (pageReqVO.getDate() != null && pageReqVO.getDate().length >= 2) {
            return success(sspSlotDayService.getSspSlotDayTrend(pageReqVO));
        }
        // 未传时间范围时，默认返回最近 7 天数据
        SspSlotDayPageReqVO defaultReqVO = buildDefaultWeekReqVO(pageReqVO);
        return success(sspSlotDayService.getSspSlotDayTrend(defaultReqVO));
    }

    /**
     * 当未指定时间范围时，默认取最近 7 天（含今天）
     * @param reqVO 原始查询条件（不含时间范围）
     * @return 补全默认时间范围的查询条件
     */
    private SspSlotDayPageReqVO buildDefaultWeekReqVO(SspSlotDayPageReqVO reqVO) {
        // 计算最近 7 天的起止日期（yyyyMMdd）
        java.time.LocalDate today = java.time.LocalDate.now();
        java.time.LocalDate start = today.minusDays(6);
        String startStr = start.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        String endStr = today.format(java.time.format.DateTimeFormatter.ofPattern("yyyyMMdd"));
        reqVO.setDate(new String[]{startStr, endStr});
        return reqVO;
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP-SSP广告位报 Excel")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSspSlotDayExcel(@Valid SspSlotDayPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SspSlotDayDO> list = sspSlotDayService.getSspSlotDayPage(pageReqVO).getList();

        list.forEach(sspSlotDayDO -> {

            BigDecimal divisor = BigDecimal.valueOf(100000);

            BigDecimal income = sspSlotDayDO.getIncome();
            BigDecimal spend = sspSlotDayDO.getSpend();

            sspSlotDayDO.setTotalIncome(
                    income.add(spend).divide(divisor, 2, RoundingMode.HALF_UP)
            );

            sspSlotDayDO.setSpend(
                    spend.divide(divisor, 2, RoundingMode.HALF_UP)
            );

            sspSlotDayDO.setIncome(
                    income.divide(divisor, 2, RoundingMode.HALF_UP)
            );

        });

        // 导出 Excel
        ExcelUtils.write(response, "DSP-SSP广告位报.xls", "数据", SspSlotDayRespVO.class,
                        BeanUtils.toBean(list, SspSlotDayRespVO.class));
    }

}
