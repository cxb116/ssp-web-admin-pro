package cn.iocoder.yudao.module.ssp.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 媒体 Response VO")
@Data
@ExcelIgnoreUnannotated
public class MediaRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "803")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "账号名", requiredMode = Schema.RequiredMode.REQUIRED, example = "7880")
    @ExcelProperty("账号名")
    private String account;

    @Schema(description = "公司简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("公司简称")
    private String mediaCompanyShort;

    @Schema(description = "媒体名称", example = "芋艿")
    @ExcelProperty("媒体名称")
    private String name;

    @Schema(description = "公司名称", example = "李四")
    @ExcelProperty("公司名称")
    private String mediaCompanyName;

    @Schema(description = "公司地址")
    @ExcelProperty("公司地址")
    private String mediaCompanyAddress;

    @Schema(description = "法人姓名", example = "张三")
    @ExcelProperty("法人姓名")
    private String mediaOwnerName;

    @Schema(description = "联系人", example = "张三")
    @ExcelProperty("联系人")
    private String contactName;

    @Schema(description = "联系电话")
    @ExcelProperty("联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    @ExcelProperty("联系邮箱")
    private String contactEmail;

    @Schema(description = "接入方式", example = "1")
    @ExcelProperty(value = "接入方式", converter = DictConvert.class)
    @DictFormat("ssp_access_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer accessType;

    @Schema(description = "流量类型", example = "2")
    @ExcelProperty(value = "流量类型", converter = DictConvert.class)
    @DictFormat("ssp_traffic_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer trafficType;

    @Schema(description = "媒体状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "媒体状态", converter = DictConvert.class)
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