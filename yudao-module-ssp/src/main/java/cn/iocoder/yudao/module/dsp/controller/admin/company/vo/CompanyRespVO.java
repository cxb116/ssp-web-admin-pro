package cn.iocoder.yudao.module.dsp.controller.admin.company.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 预算广告 Response VO")
@Data
@ExcelIgnoreUnannotated
public class CompanyRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32229")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @ExcelProperty("公司名称")
    private String name;

    @Schema(description = "预算映射值", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("预算映射值")
    private Long dspCode;

    @Schema(description = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @ExcelProperty("请求地址")
    private String url;

    @Schema(description = "请求方法")
    @ExcelProperty(value = "请求方法", converter = DictConvert.class)
    @DictFormat("ssp_request_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer method;

    @Schema(description = "超时时间")
    @ExcelProperty("超时时间")
    private Integer timeout;

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