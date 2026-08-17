package cn.iocoder.yudao.module.data.controller.admin.dspslotday;

import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.SspSlotDayPageReqVO;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.SspSlotDayRespVO;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
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
    public CommonResult<DspSlotDayRespVO> getDspSlotDaySum(@RequestParam("date") Long date) {
        DspSlotDayRespVO result = dspSlotDayService.getDspSlotDaySum(date);
        return success(result);
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
        // 导出 Excel
        ExcelUtils.write(response, "DSP预算广告位日期报.xls", "数据", DspSlotDayRespVO.class,
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

}