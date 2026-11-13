package cn.iocoder.yudao.module.domain.controller.admin.domain;

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

import cn.iocoder.yudao.module.domain.controller.admin.domain.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domain.DomainDO;
import cn.iocoder.yudao.module.domain.service.domain.DomainService;

@Tag(name = "管理后台 - DSP域名管理")
@RestController
@RequestMapping("/domain/domain")
@Validated
public class DomainController {

    @Resource
    private DomainService domainService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP域名管理")
    @PreAuthorize("@ss.hasPermission('domain:domain:create')")
    public CommonResult<Long> createDomain(@Valid @RequestBody DomainSaveReqVO createReqVO) {
        return success(domainService.createDomain(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP域名管理")
    @PreAuthorize("@ss.hasPermission('domain:domain:update')")
    public CommonResult<Boolean> updateDomain(@Valid @RequestBody DomainSaveReqVO updateReqVO) {
        domainService.updateDomain(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP域名管理")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('domain:domain:delete')")
    public CommonResult<Boolean> deleteDomain(@RequestParam("id") Long id) {
        domainService.deleteDomain(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP域名管理")
                @PreAuthorize("@ss.hasPermission('domain:domain:delete')")
    public CommonResult<Boolean> deleteDomainList(@RequestParam("ids") List<Long> ids) {
        domainService.deleteDomainListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP域名管理")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('domain:domain:query')")
    public CommonResult<DomainRespVO> getDomain(@RequestParam("id") Long id) {
        DomainDO domain = domainService.getDomain(id);
        return success(BeanUtils.toBean(domain, DomainRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP域名管理分页")
    @PreAuthorize("@ss.hasPermission('domain:domain:query')")
    public CommonResult<PageResult<DomainRespVO>> getDomainPage(@Valid DomainPageReqVO pageReqVO) {
        PageResult<DomainDO> pageResult = domainService.getDomainPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DomainRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP域名管理 Excel")
    @PreAuthorize("@ss.hasPermission('domain:domain:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportDomainExcel(@Valid DomainPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DomainDO> list = domainService.getDomainPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP域名管理.xls", "数据", DomainRespVO.class,
                        BeanUtils.toBean(list, DomainRespVO.class));
    }

}