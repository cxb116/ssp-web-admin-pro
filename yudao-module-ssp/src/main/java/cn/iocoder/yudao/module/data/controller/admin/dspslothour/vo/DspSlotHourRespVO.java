package cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP预算广告位小时报 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DspSlotHourRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "31172")
    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "预算位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "20637")
    @ExcelProperty("预算位ID")
    private Long dspSlotId;

    @Schema(description = "预算广告位", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("预算广告位")
    private String dspSlotCode;

    @Schema(description = "SSP slot id", requiredMode = Schema.RequiredMode.REQUIRED, example = "26099")
    @ExcelProperty("SSP slot id")
    private Long sspSlotId;

    @Schema(description = "展示PV")
    @ExcelProperty("展示PV")
    private Long showPv;

    @Schema(description = "展示UV")
    @ExcelProperty("展示UV")
    private Long showUv;

    @Schema(description = "点击PV")
    @ExcelProperty("点击PV")
    private Long clickPv;

    @Schema(description = "点击UV")
    @ExcelProperty("点击UV")
    private Long clickUv;

    @Schema(description = "请求PV")
    @ExcelProperty("请求PV")
    private Long reqPv;

    @Schema(description = "请求UV")
    @ExcelProperty("请求UV")
    private Long reqUv;

    @Schema(description = "丢弃请求")
    @ExcelProperty("丢弃请求")
    private Long discard;

    @Schema(description = "返回PV")
    @ExcelProperty("返回PV")
    private Long retPv;

    @Schema(description = "返回UV")
    @ExcelProperty("返回UV")
    private Long retUv;

    @Schema(description = "成本(分)")
    @ExcelProperty("成本(分)")
    private Long spend;

    @Schema(description = "收入(分)")
    @ExcelProperty("收入(分)")
    private Long income;

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

    @Schema(description = "时间(yyyyMMdd / yyyyMMddHH)")
    @ExcelProperty("时间(yyyyMMdd / yyyyMMddHH)")
    private Integer date;

    @Schema(description = "创建时间戳")
    @ExcelProperty("创建时间戳")
    private Integer createdAt;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}