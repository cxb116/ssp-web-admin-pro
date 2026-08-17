package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import cn.idev.excel.annotation.write.style.ColumnWidth;
import cn.idev.excel.annotation.write.style.HeadFontStyle;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import cn.idev.excel.annotation.*;

import java.util.Date;

@Schema(description = "管理后台 - DSP数据导入模板 Excel VO")
@Data
@ExcelIgnoreUnannotated
@HeadFontStyle(fontHeightInPoints = 10, fontName = "微软雅黑")
public class InputExecTemplateVO {

    @Schema(description = "导入时间", example = "2026-10-10")
    @ExcelProperty("导入时间")
    @ColumnWidth(20)
    private String inputTime;

    @Schema(description = "预算方广告位ID", example = "DSP_SLOT_001")
    @ExcelProperty("预算方广告位ID")
    @ColumnWidth(20)
    private String dspSlotCode;

    @Schema(description = "成本（元）", example = "10000")
    @ExcelProperty("收益(元)")
    @ColumnWidth(20)
    private Long spend;

    @Schema(description = "广告位ID", example = "366716")
    @ExcelProperty("广告位ID")
    @ColumnWidth(20)
    private Long sspSlotId;

    @Schema(description = "结算方式", example = "CPM")
    @ExcelProperty("结算方式")
    @ColumnWidth(10)
    private String settleType;

    @Schema(description = "结算比例", example = "0.7")
    @ExcelProperty("结算比例")
    @ColumnWidth(20)
    private Double settleRate;

    @Schema(description = "媒体公司", example = "腾讯广告")
    @ExcelProperty("媒体公司")
    @ColumnWidth(20)
    private String mediaCompany;


    private Double Percentage;  // 曝光百分比

    private Long Show; // 曝光次数

}