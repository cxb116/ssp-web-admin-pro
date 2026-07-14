package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP数据导入 Response VO")
@Data
@ExcelIgnoreUnannotated
public class InputExecRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23801")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "预算公司id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4207")
    @ExcelProperty("预算公司id")
    private Long companyId;

    @Schema(description = "导入条数")
    @ExcelProperty("导入条数")
    private Long tables;

    @Schema(description = "导入时间")
    @ExcelProperty("导入时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime inputTime;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createTime;

    @Schema(description = "修改时间")
    @ExcelProperty("修改时间")
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updateTime;


    private String companyName;
}