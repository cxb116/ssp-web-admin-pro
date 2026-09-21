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
import cn.idev.excel.write.handler.AbstractRowWriteHandler;
import cn.idev.excel.write.handler.context.RowWriteHandlerContext;
import org.apache.poi.ss.util.CellRangeAddress;

@Tag(name = "绠＄悊鍚庡彴 - DSP鏁版嵁瀵煎叆")
@RestController
@RequestMapping("/data/input-exec")
@Validated
public class InputExecController {

    @Resource
    private InputExecService inputExecService;

    @PostMapping("/create")
    @Operation(summary = "鍒涘缓DSP鏁版嵁瀵煎叆")
    @PreAuthorize("@ss.hasPermission('data:input-exec:create')")
    public CommonResult<Long> createInputExec(@Valid @RequestBody InputExecSaveReqVO createReqVO) {
        return success(inputExecService.createInputExec(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "鏇存柊DSP鏁版嵁瀵煎叆")
    @PreAuthorize("@ss.hasPermission('data:input-exec:update')")
    public CommonResult<Boolean> updateInputExec(@Valid @RequestBody InputExecSaveReqVO updateReqVO) {
        inputExecService.updateInputExec(updateReqVO);
        return success(true);
    }


    @PutMapping("/updateIncome")
    @Operation(summary = "瀵煎叆缁撴灉淇濆瓨鎻愪氦鏀剁泭")
    @PreAuthorize("@ss.hasPermission('data:input-exec:update')")
    public CommonResult<String> updateInputIncome(@Valid @RequestBody List<InputIncomeReqVO> inputIncomeReqVOList) {
        return success(inputExecService.updateInputIncomeData(inputIncomeReqVOList));
    }



    @DeleteMapping("/delete")
    @Operation(summary = "鍒犻櫎DSP鏁版嵁瀵煎叆")
    @Parameter(name = "id", description = "缂栧彿", required = true)
    @PreAuthorize("@ss.hasPermission('data:input-exec:delete')")
    public CommonResult<Boolean> deleteInputExec(@RequestParam("id") Long id) {
        inputExecService.deleteInputExec(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "缂栧彿", required = true)
    @Operation(summary = "鎵归噺鍒犻櫎DSP鏁版嵁瀵煎叆")
                @PreAuthorize("@ss.hasPermission('data:input-exec:delete')")
    public CommonResult<Boolean> deleteInputExecList(@RequestParam("ids") List<Long> ids) {
        inputExecService.deleteInputExecListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "鑾峰緱DSP鏁版嵁瀵煎叆")
    @Parameter(name = "id", description = "缂栧彿", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('data:input-exec:query')")
    public CommonResult<InputExecRespVO> getInputExec(@RequestParam("id") Long id) {
        InputExecDO inputExec = inputExecService.getInputExec(id);
        return success(BeanUtils.toBean(inputExec, InputExecRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "鑾峰緱DSP鏁版嵁瀵煎叆鍒嗛〉")
    @PreAuthorize("@ss.hasPermission('data:input-exec:query')")
    public CommonResult<PageResult<InputExecRespVO>> getInputExecPage(@Valid InputExecPageReqVO pageReqVO) {
        PageResult<InputExecDO> pageResult = inputExecService.getInputExecPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, InputExecRespVO.class));
    }

    /***
     * 瀵煎嚭妯℃澘鏁版嵁
     * @param id 鍏徃ID
     * @param inputTime 瀵煎叆鏃堕棿
     * @param response HTTP鍝嶅簲
     */
    @GetMapping("/down-excel-input")
    @Operation(summary = "涓嬭浇妯℃澘")
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    public void downExcelInput(@RequestParam("id") Long id,
                               @RequestParam("inputTime") String inputTime,
                               HttpServletResponse response) throws IOException {
        List<InputExecDO> downExcelInput = inputExecService.getDownExcelInput(id, inputTime);

        // 杞崲涓烘ā鏉縑O
        List<InputExecTemplateVO> templateList = downExcelInput.stream().map(exec -> {
            InputExecTemplateVO vo = new InputExecTemplateVO();
            vo.setInputTime(inputTime);
            vo.setDspSlotCode(exec.getDspSlotCode());
//            vo.setSspSlotId(exec.getSspSlotId());
//            vo.setMediaCompany(exec.getMediaCompany());
//            vo.setSettleType("鍒嗘垚");
//            vo.setSettleRate(80.0);
            return vo;
        }).sorted(Comparator.comparing(InputExecTemplateVO::getDspSlotCode,
                Comparator.nullsLast(String::compareTo))).collect(java.util.stream.Collectors.toList());

        // 瀵煎嚭 Excel 妯℃澘
        ExcelUtils.write(response, "棰勭畻鏁版嵁瀵煎叆妯℃澘.xls", "妯℃澘鏁版嵁", InputExecTemplateVO.class, templateList,
                false, new DspSlotCodeMergeHandler(templateList));
    }


    @GetMapping("/export-excel")
    @Operation(summary = "瀵煎嚭DSP鏁版嵁瀵煎叆 Excel")
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportInputExecExcel(@Valid InputExecPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<InputExecDO> list = inputExecService.getInputExecPage(pageReqVO).getList();
        // 瀵煎嚭 Excel
        ExcelUtils.write(response, "DSP鏁版嵁瀵煎叆.xls", "鏁版嵁", InputExecRespVO.class,
                        BeanUtils.toBean(list, InputExecRespVO.class));
    }

    @PostMapping("/import")
    @Operation(summary = "瀵煎叆DSP鏁版嵁瀵煎叆 Excel")
    @Parameters({
            @Parameter(name = "file", description = "Excel 鏂囦欢", required = true),
            @Parameter(name = "id", description = "鍏徃ID", required = true)
    })
    @PreAuthorize("@ss.hasPermission('data:input-exec:export')")
    public CommonResult<List<DspSlotDayInputRespVo>> importExecExcel(@RequestParam("file") MultipartFile file,
                                                                       @RequestParam("id") Long id) throws Exception {
        try {
            List<InputExecTemplateVO> list = ExcelUtils.read(file, InputExecTemplateVO.class);
            List<DspSlotDayInputRespVo> result = inputExecService.importExecList(list, id);
            return success(result);
        } catch (Exception e) {
            // 保留原有导入处理逻辑；发生异常时将包含行号的错误信息直接返回前端，便于定位失败记录。
            String message = e.getMessage();
            if (message == null || message.trim().isEmpty()) {
                message = "Excel 导入失败，请检查失败行数据";
            } else {
                message = "Excel 导入失败：" + message;
            }
            return CommonResult.error(500, message);
        }
    }

    /**
     * 灏嗙浉鍚岄绠楁柟骞垮憡浣?ID 鐨勮繛缁暟鎹鍚堝苟锛涙敹鐩婂睘浜庤骞垮憡浣嶏紝鍥犳鍚屾鍚堝苟鏀剁泭鍒椼€?     */
    private static class DspSlotCodeMergeHandler extends AbstractRowWriteHandler {

        private static final int DSP_SLOT_CODE_COLUMN = 1;
        private static final int SPEND_COLUMN = 2;
        private final List<InputExecTemplateVO> data;
        private int groupStartRow = 1; // 绗?0 琛屾槸琛ㄥご

        private DspSlotCodeMergeHandler(List<InputExecTemplateVO> data) {
            this.data = data;
        }

        @Override
        public void afterRowDispose(RowWriteHandlerContext context) {
            if (Boolean.TRUE.equals(context.getHead()) || context.getRelativeRowIndex() == null) {
                return;
            }
            int dataIndex = context.getRelativeRowIndex();
            boolean groupEnds = dataIndex == data.size() - 1
                    || !Objects.equals(data.get(dataIndex).getDspSlotCode(), data.get(dataIndex + 1).getDspSlotCode());
            if (!groupEnds) {
                return;
            }
            int groupEndRow = context.getRowIndex();
            if (groupEndRow > groupStartRow) {
                context.getWriteSheetHolder().getSheet().addMergedRegion(
                        new CellRangeAddress(groupStartRow, groupEndRow, DSP_SLOT_CODE_COLUMN, DSP_SLOT_CODE_COLUMN));
                context.getWriteSheetHolder().getSheet().addMergedRegion(
                        new CellRangeAddress(groupStartRow, groupEndRow, SPEND_COLUMN, SPEND_COLUMN));
            }
            groupStartRow = groupEndRow + 1;
        }
    }

}

