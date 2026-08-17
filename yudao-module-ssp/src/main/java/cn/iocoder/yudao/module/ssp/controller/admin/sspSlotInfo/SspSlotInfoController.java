package cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo;

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

import cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.service.sspSlotInfo.SspSlotInfoService;

@Tag(name = "管理后台 - 媒体广告位")
@RestController
@RequestMapping("/ssp/slot-info")
@Validated
public class SspSlotInfoController {

    @Resource
    private SspSlotInfoService slotInfoService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体广告位")
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:create')")
    public CommonResult<Long> createSlotInfo(@Valid @RequestBody SspSlotInfoSaveReqVO createReqVO) {
        return success(slotInfoService.createSlotInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体广告位")
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:update')")
    public CommonResult<Boolean> updateSlotInfo(@Valid @RequestBody SspSlotInfoUpdateReqVO updateReqVO) {
        slotInfoService.updateSlotInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体广告位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfo(@RequestParam("id") Long id) {
        slotInfoService.deleteSlotInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除媒体广告位")
                @PreAuthorize("@ss.hasPermission('ssp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfoList(@RequestParam("ids") List<Long> ids) {
        slotInfoService.deleteSlotInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体广告位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:query')")
    public CommonResult<SspSlotInfoRespVO> getSlotInfo(@RequestParam("id") Long id) {
        SspSlotInfoDO slotInfo = slotInfoService.getSlotInfo(id);
        return success(BeanUtils.toBean(slotInfo, SspSlotInfoRespVO.class));
    }


    @GetMapping("/page")
    @Operation(summary = "获得媒体广告位分页")
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:query')")
    public CommonResult<PageResult<SspSlotInfoRespVO>> getSlotInfoPage(@Valid SspSlotInfoPageReqVO pageReqVO) {
        PageResult<SspSlotInfoDO> pageResult = slotInfoService.getSlotInfoPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SspSlotInfoRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出媒体广告位 Excel")
    @PreAuthorize("@ss.hasPermission('ssp:slot-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSlotInfoExcel(@Valid SspSlotInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(2000);
        List<SspSlotInfoDO> list = slotInfoService.getSlotInfoPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "媒体广告位.xls", "数据", SspSlotInfoRespVO.class,
                        BeanUtils.toBean(list, SspSlotInfoRespVO.class));
    }

}