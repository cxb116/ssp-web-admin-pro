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

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP-SSP广告位报 Excel")
    @PreAuthorize("@ss.hasPermission('data:ssp-slot-day:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSspSlotDayExcel(@Valid SspSlotDayPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SspSlotDayDO> list = sspSlotDayService.getSspSlotDayPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP-SSP广告位报.xls", "数据", SspSlotDayRespVO.class,
                        BeanUtils.toBean(list, SspSlotDayRespVO.class));
    }

}