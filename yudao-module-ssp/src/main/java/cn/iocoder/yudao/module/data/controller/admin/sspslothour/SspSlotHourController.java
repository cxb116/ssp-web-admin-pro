package cn.iocoder.yudao.module.data.controller.admin.sspslothour;

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

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import cn.iocoder.yudao.module.data.service.sspslothour.SspSlotHourService;

@Tag(name = "管理后台 - DSP-SSP广告位报")
@RestController
@RequestMapping("/data/ssp-slot-hour")
@Validated
public class SspSlotHourController {

    @Resource
    private SspSlotHourService sspSlotHourService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP-SSP广告位报")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:create')")
    public CommonResult<Long> createSspSlotHour(@Valid @RequestBody SspSlotHourSaveReqVO createReqVO) {
        return success(sspSlotHourService.createSspSlotHour(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP-SSP广告位报")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:update')")
    public CommonResult<Boolean> updateSspSlotHour(@Valid @RequestBody SspSlotHourSaveReqVO updateReqVO) {
        sspSlotHourService.updateSspSlotHour(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP-SSP广告位报")
    @Parameter(name = "id", description = "编号", required = true)
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:delete')")
    public CommonResult<Boolean> deleteSspSlotHour(@RequestParam("id") Long id) {
        sspSlotHourService.deleteSspSlotHour(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP-SSP广告位报")
//                @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:delete')")
    public CommonResult<Boolean> deleteSspSlotHourList(@RequestParam("ids") List<Long> ids) {
        sspSlotHourService.deleteSspSlotHourListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP-SSP广告位报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:query')")
    public CommonResult<SspSlotHourRespVO> getSspSlotHour(@RequestParam("id") Long id) {
        SspSlotHourDO sspSlotHour = sspSlotHourService.getSspSlotHour(id);
        return success(BeanUtils.toBean(sspSlotHour, SspSlotHourRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP-SSP广告位报分页")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:query')")
    public CommonResult<PageResult<SspSlotHourRespVO>> getSspSlotHourPage(@Valid SspSlotHourPageReqVO pageReqVO) {
        PageResult<SspSlotHourDO> pageResult = sspSlotHourService.getSspSlotHourPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SspSlotHourRespVO.class));
    }

    @GetMapping("/dsp_ssp_hour")
    @Operation(summary = "获得DSP预算位对应的SSP小时子表数据")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:query')")
    public CommonResult<List<SspSlotHourRespVO>> getDspSspSlotHour(
            @RequestParam("dspSlotId") Long dspSlotId,
            @RequestParam("date") Long date) {
        List<SspSlotHourDO> list = sspSlotHourService.getSspDspSlotHour(dspSlotId, date);
        return success(BeanUtils.toBean(list, SspSlotHourRespVO.class));
    }

    @GetMapping("/trend")
    @Operation(summary = "小时报表折线图（单天0~23点按小时聚合）")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:query')")
    public CommonResult<List<SspSlotHourTrendRespVO>> getSspSlotHourTrend(@Valid SspSlotHourPageReqVO pageReqVO) {
        // 未指定日期范围时，默认查询今天（yyyyMMdd00 ~ yyyyMMdd23）
        if (pageReqVO.getDate() == null || pageReqVO.getDate().length == 0) {
            pageReqVO.setDate(buildTodayHourRange());
        }
        return success(sspSlotHourService.getSspSlotHourTrend(pageReqVO));
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
    @Operation(summary = "导出DSP-SSP广告位报 Excel")
//    @PreAuthorize("@ss.hasPermission('data:ssp-slot-hour:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSspSlotHourExcel(@Valid SspSlotHourPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SspSlotHourDO> list = sspSlotHourService.getSspSlotHourPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP-SSP广告位报.xls", "数据", SspSlotHourRespVO.class,
                        BeanUtils.toBean(list, SspSlotHourRespVO.class));
    }

}