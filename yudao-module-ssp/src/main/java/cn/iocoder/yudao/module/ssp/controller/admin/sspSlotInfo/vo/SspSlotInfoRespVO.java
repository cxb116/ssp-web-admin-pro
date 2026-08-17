package cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 媒体广告位 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SspSlotInfoRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4229")
    @ExcelProperty("媒体广告位ID")
    private Long id;

    @Schema(description = "媒体id", requiredMode = Schema.RequiredMode.REQUIRED, example = "2958")
//    @ExcelProperty("媒体id")
    private Long mediaId;




    @Schema(description = "广告位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @ExcelProperty("广告位名称")
    private String name;

    @Schema(description = "内部广告位名称")
    @ExcelProperty("内部广告位名称")
    private String nameAlise;


    @Schema(description = "媒体名称")
    private String mediaName;

    @ExcelProperty("媒体名称")
    @Schema(description = "媒体简称")
    private String mediaShortName;


    @Schema(description = "应用名称")
    @ExcelProperty("应用名称")
    private String appName;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19705")
//    @ExcelProperty("应用id")
    private Long appId;

    @Schema(description = "接入方式")
    @ExcelProperty(value = "接入方式", converter = DictConvert.class)
    @DictFormat("ssp_access_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer accessType;


    @Schema(description = "操作系统")
    @ExcelProperty(value = "操作系统", converter = DictConvert.class)
    @DictFormat("ssp_os_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer osType;

    @Schema(description = "广告场景", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "广告场景", converter = DictConvert.class)
    @DictFormat("ssp_ad_scene") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long adScene;

//    @Schema(description = "样式尺寸")
//    @ExcelProperty(value = "样式尺寸", converter = DictConvert.class)
//    @DictFormat("ssp_ad_size") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
//    private Long adSize;

    @Schema(description = "结算方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "结算方式", converter = DictConvert.class)
    @DictFormat("ssp_pay_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer sspPayType;

    @Schema(description = "分成系数")
//    @ExcelProperty("分成系数")
    private Integer sspDealRatio;

    @Schema(description = "固价", example = "22859")
//    @ExcelProperty("固价")
    private Integer fixedPrice;

    @Schema(description = "广告位状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "广告位状态", converter = DictConvert.class)
    @DictFormat("ssp_enable") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer enable;




    @Schema(description = "绑定条数")
    private Integer ls;

    @Schema(description = "请求数")
    private Long reqCount;

//    @ExcelProperty("媒体名称")
//    @Schema(description = "媒体名称")
//    private String mediaName;

    @Schema(description = "SSP内部名称")
    private String sspName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}