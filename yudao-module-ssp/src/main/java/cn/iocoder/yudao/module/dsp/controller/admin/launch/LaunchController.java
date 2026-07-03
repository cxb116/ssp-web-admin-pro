package cn.iocoder.yudao.module.dsp.controller.admin.launch;

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

import cn.iocoder.yudao.module.dsp.controller.admin.launch.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.service.launch.LaunchService;

@Tag(name = "管理后台 - 媒体预算绑定")
@RestController
@RequestMapping("/dsp/launch")
@Validated
public class LaunchController {

    @Resource
    private LaunchService launchService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体预算绑定")
    @PreAuthorize("@ss.hasPermission('dsp:launch:create')")
    public CommonResult<Long> createLaunch(@Valid @RequestBody LaunchSaveReqVO createReqVO) {
        return success(launchService.createLaunch(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体预算绑定")
    @PreAuthorize("@ss.hasPermission('dsp:launch:update')")
    public CommonResult<Boolean> updateLaunch(@Valid @RequestBody LaunchSaveReqVO updateReqVO) {
        launchService.updateLaunch(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体预算绑定")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dsp:launch:delete')")
    public CommonResult<Boolean> deleteLaunch(@RequestParam("id") Long id) {
        launchService.deleteLaunch(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除媒体预算绑定")
                @PreAuthorize("@ss.hasPermission('dsp:launch:delete')")
    public CommonResult<Boolean> deleteLaunchList(@RequestParam("ids") List<Long> ids) {
        launchService.deleteLaunchListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体预算绑定")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:launch:query')")
    public CommonResult<LaunchRespVO> getLaunch(@RequestParam("id") Long id) {
        LaunchDO launch = launchService.getLaunch(id);
        return success(BeanUtils.toBean(launch, LaunchRespVO.class));
    }

    // 这个id 是sspSlotId
    @GetMapping("/sspslotid/{id}")
    @Operation(summary = "获得媒体绑定的预算数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:launch:query')")
    public CommonResult<List<LaunchRespVO>> getLaunchSspSlotIdQuery(@PathVariable("id") Long id) {
        List<LaunchDO> launchList = launchService.getLaunchSspSlotIdQuery(id);
        return success(BeanUtils.toBean(launchList, LaunchRespVO.class));
    }


    // 这个id 是dspSlotId
    @GetMapping("/dspslotid/{id}")
    @Operation(summary = "获得预算绑定的媒体数据")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:launch:query')")
    public CommonResult<List<LaunchRespVO>> getLaunchDspSlotIdQuery(@PathVariable("id") Long id) {
        List<LaunchDO> launchList = launchService.getLaunchDspSlotIdQuery(id);
        return success(BeanUtils.toBean(launchList, LaunchRespVO.class));
    }


    // sspSlotId 去查 dspLaunch 中sspSlotId 中的绑定数据集合
    @GetMapping("/sspslotid_group/{id}")
    @Operation(summary = "获得DspLaunch 中的SspSlotId 的数据绑定集合")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:launch:query')")
    public CommonResult<List<LaunchRespVO>> getLaunchSspSlotList(@PathVariable("id") Long id) {
        List<LaunchDO> launchList = launchService.getLaunchSspSlotList(id);
        return success(BeanUtils.toBean(launchList, LaunchRespVO.class));
    }



    @GetMapping("/page")
    @Operation(summary = "获得媒体预算绑定分页")
    @PreAuthorize("@ss.hasPermission('dsp:launch:query')")
    public CommonResult<PageResult<LaunchRespVO>> getLaunchPage(@Valid LaunchPageReqVO pageReqVO) {
        PageResult<LaunchDO> pageResult = launchService.getLaunchPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, LaunchRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出媒体预算绑定 Excel")
    @PreAuthorize("@ss.hasPermission('dsp:launch:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportLaunchExcel(@Valid LaunchPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<LaunchDO> list = launchService.getLaunchPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "媒体预算绑定.xls", "数据", LaunchRespVO.class,
                        BeanUtils.toBean(list, LaunchRespVO.class));
    }

}