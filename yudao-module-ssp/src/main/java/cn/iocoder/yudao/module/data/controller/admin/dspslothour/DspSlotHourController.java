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
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:create')")
    public CommonResult<Long> createDspSlotHour(@Valid @RequestBody DspSlotHourSaveReqVO createReqVO) {
        return success(dspSlotHourService.createDspSlotHour(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP预算广告位小时报")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:update')")
    public CommonResult<Boolean> updateDspSlotHour(@Valid @RequestBody DspSlotHourSaveReqVO updateReqVO) {
        dspSlotHourService.updateDspSlotHour(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP预算广告位小时报")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:delete')")
    public CommonResult<Boolean> deleteDspSlotHour(@RequestParam("id") Long id) {
        dspSlotHourService.deleteDspSlotHour(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP预算广告位小时报")
                @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:delete')")
    public CommonResult<Boolean> deleteDspSlotHourList(@RequestParam("ids") List<Long> ids) {
        dspSlotHourService.deleteDspSlotHourListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP预算广告位小时报")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<DspSlotHourRespVO> getDspSlotHour(@RequestParam("id") Long id) {
        DspSlotHourDO dspSlotHour = dspSlotHourService.getDspSlotHour(id);
        return success(BeanUtils.toBean(dspSlotHour, DspSlotHourRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP预算广告位小时报分页")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:query')")
    public CommonResult<PageResult<DspSlotHourRespVO>> getDspSlotHourPage(@Valid DspSlotHourPageReqVO pageReqVO) {
        PageResult<DspSlotHourDO> pageResult = dspSlotHourService.getDspSlotHourPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotHourRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP预算广告位小时报 Excel")
    @PreAuthorize("@ss.hasPermission('data:dsp-slot-hour:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDspSlotHourExcel(@Valid DspSlotHourPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DspSlotHourDO> list = dspSlotHourService.getDspSlotHourPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP预算广告位小时报.xls", "数据", DspSlotHourRespVO.class,
                        BeanUtils.toBean(list, DspSlotHourRespVO.class));
    }

}