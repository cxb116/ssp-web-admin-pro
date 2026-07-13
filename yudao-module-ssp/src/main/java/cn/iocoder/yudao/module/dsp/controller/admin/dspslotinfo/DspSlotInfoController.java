package cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo;

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

import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.service.dspslotinfo.DspSlotInfoService;

@Tag(name = "管理后台 - 预算广告位")
@RestController
@RequestMapping("/dsp/slot-info")
@Validated
public class DspSlotInfoController {

    @Resource
    private DspSlotInfoService slotInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建预算广告位")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:create')")
    public CommonResult<Long> createSlotInfo(@Valid @RequestBody DspSlotInfoSaveReqVO createReqVO) {
        return success(slotInfoService.createSlotInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新预算广告位")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:update')")
    public CommonResult<Boolean> updateSlotInfo(@Valid @RequestBody DspSlotInfoSaveReqVO updateReqVO) {
        slotInfoService.updateSlotInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除预算广告位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfo(@RequestParam("id") Long id) {
        slotInfoService.deleteSlotInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除预算广告位")
                @PreAuthorize("@ss.hasPermission('dsp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfoList(@RequestParam("ids") List<Long> ids) {
        slotInfoService.deleteSlotInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得预算广告位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<DspSlotInfoRespVO> getSlotInfo(@RequestParam("id") Long id) {
        DspSlotInfoDO slotInfo = slotInfoService.getSlotInfo(id);
        return success(BeanUtils.toBean(slotInfo, DspSlotInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得预算广告位分页")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<PageResult<DspSlotInfoRespVO>> getSlotInfoPage(@Valid DspSlotInfoPageReqVO pageReqVO) {
        PageResult<DspSlotInfoDO> pageResult = slotInfoService.getSlotInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DspSlotInfoRespVO.class));
    }

    @GetMapping("/page-ssp")
    @Operation(summary = "获得媒体广告位子表数据")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<List<DspSlotInfoRespVO>> getSlotInfoPageSsp(@Valid DspSlotInfoPageReqVO pageReqVO) {
        List<DspSlotInfoRespVO> slotInfoDOList = slotInfoService.getSlotInfoPageSsp(pageReqVO);
        return success(BeanUtils.toBean(slotInfoDOList,DspSlotInfoRespVO.class));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出预算广告位 Excel")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSlotInfoExcel(@Valid DspSlotInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DspSlotInfoDO> list = slotInfoService.getSlotInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "预算广告位.xls", "数据", DspSlotInfoRespVO.class,
                        BeanUtils.toBean(list, DspSlotInfoRespVO.class));
    }

}