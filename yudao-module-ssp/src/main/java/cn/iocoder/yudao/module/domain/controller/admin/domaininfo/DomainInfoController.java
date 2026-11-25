package cn.iocoder.yudao.module.domain.controller.admin.domaininfo;

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
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo.*;
import cn.iocoder.yudao.module.domain.service.domaininfo.DomainInfoService;

@Tag(name = "管理后台 - DSP域名详情")
@RestController
@RequestMapping("/domain/info")
@Validated
public class DomainInfoController {

    @Resource
    private DomainInfoService infoService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP域名详情")
    @PreAuthorize("@ss.hasPermission('domain:info:create')")
    public CommonResult<Long> createInfo(@Valid @RequestBody DomainInfoSaveReqVO createReqVO) {
        return success(infoService.createInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP域名详情")
    @PreAuthorize("@ss.hasPermission('domain:info:update')")
    public CommonResult<Boolean> updateInfo(@Valid @RequestBody DomainInfoSaveReqVO updateReqVO) {
        infoService.updateInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP域名详情")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('domain:info:delete')")
    public CommonResult<Boolean> deleteInfo(@RequestParam("id") Long id) {
        infoService.deleteInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP域名详情")
                @PreAuthorize("@ss.hasPermission('domain:info:delete')")
    public CommonResult<Boolean> deleteInfoList(@RequestParam("ids") List<Long> ids) {
        infoService.deleteInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP域名详情")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('domain:info:query')")
    public CommonResult<DomainInfoRespVO> getInfo(@RequestParam("id") Long id) {
        return success(infoService.getInfo(id));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP域名详情分页（按 dsp_slot_id 分组合并，一个 dsp_slot_id 只返回一条）")
    @PreAuthorize("@ss.hasPermission('domain:info:query')")
    public CommonResult<PageResult<DomainInfoRespVO>> getInfoPage(@Valid DomainInfoPageReqVO pageReqVO) {
        PageResult<DomainInfoRespVO> pageResult = infoService.getInfoPage(pageReqVO);
        return success(pageResult);
    }

    @GetMapping("/list-by-dsp-slot")
    @Operation(summary = "获得指定预算位最近的原始记录（不分组合并，一条记录即一组上报域名，默认最近100条）")
    @Parameter(name = "dspSlotId", description = "预算位编号", required = true, example = "23288")
    @Parameter(name = "limit", description = "返回条数上限（默认100，最大500）", example = "100")
    @PreAuthorize("@ss.hasPermission('domain:info:query')")
    public CommonResult<List<DomainInfoRespVO>> getInfoListByDspSlotId(
            @RequestParam("dspSlotId") Long dspSlotId,
            @RequestParam(value = "limit", required = false) Integer limit) {
        return success(infoService.getInfoListByDspSlotId(dspSlotId, limit));
    }

    @GetMapping("/proportion-analysis-today")
    @Operation(summary = "查询当日全站域名组占比分析")
    @PreAuthorize("@ss.hasPermission('domain:info:query')")
    public CommonResult<List<DomainInfoProportionAnalysisRespVO>> getTodayProportionAnalysis() {
        return success(infoService.getTodayProportionAnalysis());
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP域名详情 Excel")
    @PreAuthorize("@ss.hasPermission('domain:info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInfoExcel(@Valid DomainInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DomainInfoRespVO> list = infoService.getInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP域名详情.xls", "数据", DomainInfoRespVO.class, list);
    }

}
