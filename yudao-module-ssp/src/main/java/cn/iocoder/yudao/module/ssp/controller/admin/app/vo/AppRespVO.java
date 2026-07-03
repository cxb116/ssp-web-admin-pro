package cn.iocoder.yudao.module.ssp.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 媒体应用 Response VO")
@Data
@ExcelIgnoreUnannotated
public class AppRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17508")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "媒体Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5471")
    @ExcelProperty("媒体Id")
    private Long mediaId;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("应用名称")
    private String name;

    @Schema(description = "操作系统", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "操作系统", converter = DictConvert.class)
    @DictFormat("ssp_os_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer osType;

    @Schema(description = "接入方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "接入方式", converter = DictConvert.class)
    @DictFormat("ssp_access_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer accessType;

    @Schema(description = "包名", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("包名")
    private String pkg;

    @Schema(description = "下载地址", example = "https://www.iocoder.cn")
    @ExcelProperty("下载地址")
    private String downloadUrl;

    @Schema(description = "应用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "应用状态", converter = DictConvert.class)
    @DictFormat("ssp_enable") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer enable;

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