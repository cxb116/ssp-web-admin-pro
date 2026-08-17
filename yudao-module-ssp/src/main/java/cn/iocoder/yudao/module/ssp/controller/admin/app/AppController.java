package cn.iocoder.yudao.module.ssp.controller.admin.app;

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

import cn.iocoder.yudao.module.ssp.controller.admin.app.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import cn.iocoder.yudao.module.ssp.service.app.AppService;

@Tag(name = "管理后台 - 媒体应用")
@RestController
@RequestMapping("/ssp/app")
@Validated
public class AppController {

    @Resource
    private AppService appService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体应用")
    @PreAuthorize("@ss.hasPermission('ssp:app:create')")
    public CommonResult<Long> createApp(@Valid @RequestBody AppSaveReqVO createReqVO) {
        return success(appService.createApp(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体应用")
    @PreAuthorize("@ss.hasPermission('ssp:app:update')")
    public CommonResult<Boolean> updateApp(@Valid @RequestBody AppSaveReqVO updateReqVO) {
        appService.updateApp(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体应用")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ssp:app:delete')")
    public CommonResult<Boolean> deleteApp(@RequestParam("id") Long id) {
        appService.deleteApp(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除媒体应用")
                @PreAuthorize("@ss.hasPermission('ssp:app:delete')")
    public CommonResult<Boolean> deleteAppList(@RequestParam("ids") List<Long> ids) {
        appService.deleteAppListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体应用")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ssp:app:query')")
    public CommonResult<AppRespVO> getApp(@RequestParam("id") Long id) {
        AppDO app = appService.getApp(id);
        return success(BeanUtils.toBean(app, AppRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得媒体应用分页")
    @PreAuthorize("@ss.hasPermission('ssp:app:query')")
    public CommonResult<PageResult<AppRespVO>> getAppPage(@Valid AppPageReqVO pageReqVO) {
        PageResult<AppDO> pageResult = appService.getAppPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, AppRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出媒体应用 Excel")
    @PreAuthorize("@ss.hasPermission('ssp:app:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportAppExcel(@Valid AppPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(5000);
        List<AppDO> list = appService.getAppPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "媒体应用.xls", "数据", AppRespVO.class,
                        BeanUtils.toBean(list, AppRespVO.class));
    }






}