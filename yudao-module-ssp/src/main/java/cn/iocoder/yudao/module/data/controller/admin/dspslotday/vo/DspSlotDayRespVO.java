package cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import java.math.BigDecimal;
import java.util.*;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP预算广告位日期报 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DspSlotDayRespVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25594")
//    @ExcelProperty("主键")
    private Long id;

    @Schema(description = "时间")
    @ExcelProperty("时间")
    private Integer date;

    @Schema(description = "DSP预算名称")
    @ExcelProperty("预算位名称")
    private String dspName;

    @Schema(description = "预算位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7185")
//    @ExcelProperty("预算位ID")
    private Long dspSlotId;

    @Schema(description = "预算广告位ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("预算广告位ID")
    private String dspSlotCode;

    @Schema(description = "媒体广告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9273")
//    @ExcelProperty("媒体广告ID")
    private Long sspSlotId;


    @Schema(description = "产品名称")
    @ExcelProperty("预算产品名称")
    private String productName;

    @Schema(description = "请求PV")
    @ExcelProperty("请求PV")
    private Long reqPv;

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
    private Integer createdAt;



    @Schema(description = "公司名称")
//    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "预算公司ID")
    private Long companyId;

    @Schema(description = "媒体广告位名称")
    private String sspName;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "操作系统 1=Android 2=iOS")
    private Integer osType;

    @Schema(description = "媒体名称")
    private String mediaName;

    @Schema(description = "媒体ecpm（媒体千次展示收益）")
    @ExcelProperty("媒体ecpm")
    private Double mediaEcpm;

    @Schema(description = "ecpm（预算千次展示收益）")
    @ExcelProperty("ecpm")
    private Double ecpm;

    @Schema(description = "媒体ecprm（媒体百万请求收益）")
    @ExcelProperty("媒体ecprm")
    private Double mediaEcprm;

    @Schema(description = "ecprm（预算百万请求收益）")
    @ExcelProperty("ecprm")
    private Double ecprm;

    @Schema(description = "收益（元）")
    @ExcelProperty("收益（元）")
    private BigDecimal TotalIncome;


    @Schema(description = "成本(元)")
    @ExcelProperty("成本(元)")
    private BigDecimal spend;

    @Schema(description = "收入(元)")
    @ExcelProperty("收入(元)")
    private BigDecimal income;


    /**
     * 是否解绑 1 没有解绑 2 解绑
     */
    @Schema(description = "收入(元)")
    private int isDeleted;

}
