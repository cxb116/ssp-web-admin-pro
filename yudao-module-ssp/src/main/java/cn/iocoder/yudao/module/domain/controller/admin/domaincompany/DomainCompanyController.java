package cn.iocoder.yudao.module.domain.controller.admin.domaincompany;

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

import cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaincompany.DomainCompanyDO;
import cn.iocoder.yudao.module.domain.service.domaincompany.DomainCompanyService;

@Tag(name = "管理后台 - DSP域名公司列")
@RestController
@RequestMapping("/domain/company")
@Validated
public class DomainCompanyController {

    @Resource
    private DomainCompanyService companyService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP域名公司列")
    @PreAuthorize("@ss.hasPermission('domain:company:create')")
    public CommonResult<Long> createCompany(@Valid @RequestBody DomainCompanySaveReqVO createReqVO) {
        return success(companyService.createCompany(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP域名公司列")
    @PreAuthorize("@ss.hasPermission('domain:company:update')")
    public CommonResult<Boolean> updateCompany(@Valid @RequestBody DomainCompanySaveReqVO updateReqVO) {
        companyService.updateCompany(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP域名公司列")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('domain:company:delete')")
    public CommonResult<Boolean> deleteCompany(@RequestParam("id") Long id) {
        companyService.deleteCompany(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP域名公司列")
                @PreAuthorize("@ss.hasPermission('domain:company:delete')")
    public CommonResult<Boolean> deleteCompanyList(@RequestParam("ids") List<Long> ids) {
        companyService.deleteCompanyListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP域名公司列")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('domain:company:query')")
    public CommonResult<DomainCompanyRespVO> getCompany(@RequestParam("id") Long id) {
        DomainCompanyDO company = companyService.getCompany(id);
        return success(BeanUtils.toBean(company, DomainCompanyRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP域名公司列分页")
    @PreAuthorize("@ss.hasPermission('domain:company:query')")
    public CommonResult<PageResult<DomainCompanyRespVO>> getCompanyPage(@Valid DomainCompanyPageReqVO pageReqVO) {
        PageResult<DomainCompanyDO> pageResult = companyService.getCompanyPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, DomainCompanyRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP域名公司列 Excel")
    @PreAuthorize("@ss.hasPermission('domain:company:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportCompanyExcel(@Valid DomainCompanyPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<DomainCompanyDO> list = companyService.getCompanyPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP域名公司列.xls", "数据", DomainCompanyRespVO.class,
                        BeanUtils.toBean(list, DomainCompanyRespVO.class));
    }

}