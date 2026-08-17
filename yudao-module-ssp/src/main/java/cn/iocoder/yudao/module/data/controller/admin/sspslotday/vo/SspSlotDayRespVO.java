package cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP-SSP广告位报 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SspSlotDayRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "19230")
//    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "日期 ")
    @ExcelProperty("日期 ")
    private Long date;

    @Schema(description = "媒体公司简称")
    @ExcelProperty("媒体简称")
    private String mediaName;


    @Schema(description = "媒体广告位名称")
    @ExcelProperty("媒体广告位名称")
    private String sspName;

    @Schema(description = "媒体用户Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "27526")
//    @ExcelProperty("媒体用户Id")
    private Long mediaId;

    @Schema(description = "应用ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11968")
//    @ExcelProperty("应用ID")
    private Long appId;

    @Schema(description = "SSP广告位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "25423")
    @ExcelProperty("媒体广告位ID")
    private Long sspSlotId;

    @Schema(description = "应用名称")
    @ExcelProperty("应用名称")
    private String appName;

    @Schema(description = "DSP广告位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32391")
//    @ExcelProperty("DSP广告位ID")
    private Long dspSlotId;

    @Schema(description = "预算广告位编号", requiredMode = Schema.RequiredMode.REQUIRED)
//    @ExcelProperty("预算广告位编号")
    private String dspSlotCode;

    @Schema(description = "操作系统")
    @ExcelProperty("操作系统")
    private Integer osType;

    @Schema(description = "请求数", example = "20130")
    @ExcelProperty("请求PV")  //TODO 请求数
    private Long reqCount;

    @Schema(description = "丢弃请求")
    @ExcelProperty("丢弃请求")
    private Long discard;

    @Schema(description = "返回PV")
    @ExcelProperty("返回PV")
    private Long retPv;


    @Schema(description = "展示PV")
    @ExcelProperty("展示PV")
    private Long showPv;

    @Schema(description = "展示UV")
//    @ExcelProperty("展示UV")
    private Long showUv;

    @Schema(description = "点击PV")
    @ExcelProperty("点击PV")
    private Long clickPv;

    @Schema(description = "点击UV")
//    @ExcelProperty("点击UV")
    private Long clickUv;

    @Schema(description = "请求PV")
//    @ExcelProperty("请求PV")
    private Long reqPv;



    @Schema(description = "请求UV")
//    @ExcelProperty("请求UV")
    private Long reqUv;


    @Schema(description = "返回UV")
//    @ExcelProperty("返回UV")
    private Long retUv;


    @Schema(description = "填充率")
    @ExcelProperty("填充率")
    private Double fillRate;

    @Schema(description = "展现率")
    @ExcelProperty("展现率")
    private Double displayRate;

    @Schema(description = "点击率")
    @ExcelProperty("点击率")
    private Double clickRate;

    @Schema(description = "折后点击")
    @ExcelProperty("折后点击")
    private Long discountClickPv;

    @Schema(description = "折后展示")
    @ExcelProperty("折后展示")
    private Long discountShowPv;

    @Schema(description = "调起成功")
    @ExcelProperty("调起成功")
    private Long dplsuccPv;

    @Schema(description = "完成量")
    @ExcelProperty("完成量")
    private Long completePv;

    @Schema(description = "安装量")
    @ExcelProperty("安装量")
    private Long installPv;

    @Schema(description = "激活量")
    @ExcelProperty("激活量")
    private Long activatePv;

    @Schema(description = "创建时间戳")
//    @ExcelProperty("创建时间戳")
    private Long createdAt;

    @Schema(description = "媒体ecpm（媒体千次展示收益）")
    @ExcelProperty("媒体ecpm")
    private Long mediaEcpm;


    @Schema(description = "ecpm（预算千次展示收益）")
    @ExcelProperty("ecpm")
    private Long ecpm;

    @Schema(description = "媒体ecprm（媒体百万请求收益）")
    @ExcelProperty("媒体ecprm")
    private Long mediaEcprm;

    @Schema(description = "ecprm（预算百万请求收益）")
    @ExcelProperty("ecprm")
    private Long ecprm;


    @Schema(description = "成本(分)")
    @ExcelProperty("成本(分)")
    private Long spend;

    @Schema(description = "收入(分)")
    @ExcelProperty("收入(分)")
    private Long income;

}