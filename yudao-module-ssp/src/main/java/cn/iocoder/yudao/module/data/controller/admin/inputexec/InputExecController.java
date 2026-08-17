package cn.iocoder.yudao.module.data.controller.admin.inputexec;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Parameters;
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

import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.module.data.service.inputexec.InputExecService;
import org.springframework.web.multipart.MultipartFile;

@Tag(name = "管理后台 - DSP数据导入")
@RestController
@RequestMapping("/data/input-exec")
@Validated
public class InputExecController {

    @Resource
    private InputExecService inputExecService;

    @PostMapping("/create")
    @Operation(summary = "创建DSP数据导入")
    @PreAuthorize("@ss.hasPermission('data:input-exec:create')")
    public CommonResult<Long> createInputExec(@Valid @RequestBody InputExecSaveReqVO createReqVO) {
        return success(inputExecService.createInputExec(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新DSP数据导入")
    @PreAuthorize("@ss.hasPermission('data:input-exec:update')")
    public CommonResult<Boolean> updateInputExec(@Valid @RequestBody InputExecSaveReqVO updateReqVO) {
        inputExecService.updateInputExec(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除DSP数据导入")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('data:input-exec:delete')")
    public CommonResult<Boolean> deleteInputExec(@RequestParam("id") Long id) {
        inputExecService.deleteInputExec(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除DSP数据导入")
                @PreAuthorize("@ss.hasPermission('data:input-exec:delete')")
    public CommonResult<Boolean> deleteInputExecList(@RequestParam("ids") List<Long> ids) {
        inputExecService.deleteInputExecListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得DSP数据导入")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:input-exec:query')")
    public CommonResult<InputExecRespVO> getInputExec(@RequestParam("id") Long id) {
        InputExecDO inputExec = inputExecService.getInputExec(id);
        return success(BeanUtils.toBean(inputExec, InputExecRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得DSP数据导入分页")
    @PreAuthorize("@ss.hasPermission('data:input-exec:query')")
    public CommonResult<PageResult<InputExecRespVO>> getInputExecPage(@Valid InputExecPageReqVO pageReqVO) {
        PageResult<InputExecDO> pageResult = inputExecService.getInputExecPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, InputExecRespVO.class));
    }

    /***
     * 导出模板数据
     * @param id 公司ID
     * @param inputTime 导入时间
     * @param response HTTP响应
     */
    @GetMapping("/down-excel-input")
    @Operation(summary = "下载模板")
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    public void downExcelInput(@RequestParam("id") Long id,
                               @RequestParam("inputTime") String inputTime,
                               HttpServletResponse response) throws IOException {
        List<InputExecDO> downExcelInput = inputExecService.getDownExcelInput(id, inputTime);

        // 转换为模板VO
        List<InputExecTemplateVO> templateList = downExcelInput.stream().map(exec -> {
            InputExecTemplateVO vo = new InputExecTemplateVO();
            vo.setInputTime(inputTime);
            vo.setDspSlotCode(exec.getDspSlotCode());
            vo.setSspSlotId(exec.getSspSlotId());
            vo.setMediaCompany(exec.getMediaCompany());
            vo.setSettleType("分成");
            return vo;
        }).collect(java.util.stream.Collectors.toList());

        // 导出 Excel 模板
        ExcelUtils.write(response, "DSP数据导入模板.xls", "模板数据", InputExecTemplateVO.class, templateList, false);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出DSP数据导入 Excel")
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInputExecExcel(@Valid InputExecPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<InputExecDO> list = inputExecService.getInputExecPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "DSP数据导入.xls", "数据", InputExecRespVO.class,
                        BeanUtils.toBean(list, InputExecRespVO.class));
    }

    @PostMapping("/import")
    @Operation(summary = "导入DSP数据导入 Excel")
    @Parameters({
            @Parameter(name = "file", description = "Excel 文件", required = true),
            @Parameter(name = "id", description = "公司ID", required = true)
    })
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    public CommonResult<String> importExecExcel(@RequestParam("file") MultipartFile file,
                                                 @RequestParam("id") Long id) throws Exception {
        List<InputExecTemplateVO> list = ExcelUtils.read(file, InputExecTemplateVO.class);
        String result = inputExecService.importExecList(list, id);
        return success(result);
    }

}