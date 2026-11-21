package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist;

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

import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklist.SlotBlacklistDO;
import cn.iocoder.yudao.module.ssp.service.slotblacklist.SlotBlacklistService;

@Tag(name = "管理后台 - 黑名单配置")
@RestController
@RequestMapping("/ssp/slot-blacklist")
@Validated
public class SlotBlacklistController {

    @Resource
    private SlotBlacklistService slotBlacklistService;

    @PostMapping("/create")
    @Operation(summary = "创建黑名单配置")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:create')")
    public CommonResult<Long> createSlotBlacklist(@Valid @RequestBody SlotBlacklistSaveReqVO createReqVO) {
        return success(slotBlacklistService.createSlotBlacklist(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新黑名单配置")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:update')")
    public CommonResult<Boolean> updateSlotBlacklist(@Valid @RequestBody SlotBlacklistSaveReqVO updateReqVO) {
        slotBlacklistService.updateSlotBlacklist(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除黑名单配置")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:delete')")
    public CommonResult<Boolean> deleteSlotBlacklist(@RequestParam("id") Long id) {
        slotBlacklistService.deleteSlotBlacklist(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除黑名单配置")
                @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:delete')")
    public CommonResult<Boolean> deleteSlotBlacklistList(@RequestParam("ids") List<Long> ids) {
        slotBlacklistService.deleteSlotBlacklistListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得黑名单配置")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:query')")
    public CommonResult<SlotBlacklistRespVO> getSlotBlacklist(@RequestParam("id") Long id) {
        SlotBlacklistDO slotBlacklist = slotBlacklistService.getSlotBlacklist(id);
        return success(BeanUtils.toBean(slotBlacklist, SlotBlacklistRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得黑名单配置分页")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:query')")
    public CommonResult<PageResult<SlotBlacklistRespVO>> getSlotBlacklistPage(@Valid SlotBlacklistPageReqVO pageReqVO) {
        PageResult<SlotBlacklistDO> pageResult = slotBlacklistService.getSlotBlacklistPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SlotBlacklistRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出黑名单配置 Excel")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSlotBlacklistExcel(@Valid SlotBlacklistPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SlotBlacklistDO> list = slotBlacklistService.getSlotBlacklistPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "黑名单配置.xls", "数据", SlotBlacklistRespVO.class,
                        BeanUtils.toBean(list, SlotBlacklistRespVO.class));
    }

}