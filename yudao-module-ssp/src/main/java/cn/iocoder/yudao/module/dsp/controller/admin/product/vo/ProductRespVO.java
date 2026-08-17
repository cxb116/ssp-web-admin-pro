package cn.iocoder.yudao.module.dsp.controller.admin.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 预算产品 Response VO")
@Data
@ExcelIgnoreUnannotated
public class ProductRespVO {

    @Schema(description = "预算产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13801")
    @ExcelProperty("预算产品ID")
    private Long id;

    @Schema(description = "预算公司名称")
    @ExcelProperty("预算公司名称")
    private String companyName;


    @Schema(description = "预算产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @ExcelProperty("预算产品名称")
    private String name;

    @Schema(description = "公司id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14579")
//    @ExcelProperty("公司id")
    private Long companyId;

    @Schema(description = "操作系统类型 1=Android，2=iOS")
    @ExcelProperty(value = "操作系统", converter = DictConvert.class)
    @DictFormat("ssp_os_type")
    private Integer osType;


    @Schema(description = "创建者")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @ExcelProperty("更新者")
    private String updater;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;


}