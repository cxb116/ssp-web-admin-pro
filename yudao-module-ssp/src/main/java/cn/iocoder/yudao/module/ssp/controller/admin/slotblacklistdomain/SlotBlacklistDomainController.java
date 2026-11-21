package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain;

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

import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklistdomain.SlotBlacklistDomainDO;
import cn.iocoder.yudao.module.ssp.service.slotblacklistdomain.SlotBlacklistDomainService;

@Tag(name = "管理后台 - 黑名单统计")
@RestController
@RequestMapping("/ssp/slot-blacklist-domain")
@Validated
public class SlotBlacklistDomainController {

    @Resource
    private SlotBlacklistDomainService slotBlacklistDomainService;

    @PostMapping("/create")
    @Operation(summary = "创建黑名单统计")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:create')")
    public CommonResult<Long> createSlotBlacklistDomain(@Valid @RequestBody SlotBlacklistDomainSaveReqVO createReqVO) {
        return success(slotBlacklistDomainService.createSlotBlacklistDomain(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新黑名单统计")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:update')")
    public CommonResult<Boolean> updateSlotBlacklistDomain(@Valid @RequestBody SlotBlacklistDomainSaveReqVO updateReqVO) {
        slotBlacklistDomainService.updateSlotBlacklistDomain(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除黑名单统计")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:delete')")
    public CommonResult<Boolean> deleteSlotBlacklistDomain(@RequestParam("id") Long id) {
        slotBlacklistDomainService.deleteSlotBlacklistDomain(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除黑名单统计")
                @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:delete')")
    public CommonResult<Boolean> deleteSlotBlacklistDomainList(@RequestParam("ids") List<Long> ids) {
        slotBlacklistDomainService.deleteSlotBlacklistDomainListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得黑名单统计")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:query')")
    public CommonResult<SlotBlacklistDomainRespVO> getSlotBlacklistDomain(@RequestParam("id") Long id) {
        SlotBlacklistDomainDO slotBlacklistDomain = slotBlacklistDomainService.getSlotBlacklistDomain(id);
        return success(BeanUtils.toBean(slotBlacklistDomain, SlotBlacklistDomainRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得黑名单统计分页")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:query')")
    public CommonResult<PageResult<SlotBlacklistDomainRespVO>> getSlotBlacklistDomainPage(@Valid SlotBlacklistDomainPageReqVO pageReqVO) {
        PageResult<SlotBlacklistDomainDO> pageResult = slotBlacklistDomainService.getSlotBlacklistDomainPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, SlotBlacklistDomainRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出黑名单统计 Excel")
    @PreAuthorize("@ss.hasPermission('ssp:slot-blacklist-domain:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSlotBlacklistDomainExcel(@Valid SlotBlacklistDomainPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<SlotBlacklistDomainDO> list = slotBlacklistDomainService.getSlotBlacklistDomainPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "黑名单统计.xls", "数据", SlotBlacklistDomainRespVO.class,
                        BeanUtils.toBean(list, SlotBlacklistDomainRespVO.class));
    }

}