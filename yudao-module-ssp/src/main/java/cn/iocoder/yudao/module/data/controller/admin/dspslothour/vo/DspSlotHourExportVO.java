package cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo;

import cn.idev.excel.annotation.ExcelProperty;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class DspSlotHourExportVO {
    @ExcelProperty("时间")
    private String hour;

    @ExcelProperty("预算公司名称")
    private String companyName;

    @ExcelProperty("预算位名称")
    private String dspName;

    @ExcelProperty("预算位ID")
    private Long dspSlotId;

    @ExcelProperty("预算方广告位ID")
    private String dspSlotCode;

    @ExcelProperty("预算产品名称")
    private String productName;

    @ExcelProperty("请求PV")
    private Long reqPv;

    @ExcelProperty("丢弃请求")
    private Long discard;

    @ExcelProperty("返回PV")
    private Long retPv;

    @ExcelProperty("展示PV")
    private Long showPv;

    @ExcelProperty("点击PV")
    private Long clickPv;

    @ExcelProperty("填充率")
    private String fillRate;

    @ExcelProperty("展现率")
    private String displayRate;

    @ExcelProperty("点击率")
    private String clickRate;

    @ExcelProperty("折后点击")
    private Long discountClickPv;

    @ExcelProperty("折后展示")
    private Long discountShowPv;

    @ExcelProperty("媒体ecpm")
    private BigDecimal mediaEcpm;

    @ExcelProperty("ecpm")
    private BigDecimal ecpm;

    @ExcelProperty("媒体ecprm")
    private BigDecimal mediaEcprm;

    @ExcelProperty("ecprm")
    private BigDecimal ecprm;

    @ExcelProperty("收益(元)")
    private BigDecimal revenue;

    @ExcelProperty("成本(元)")
    private BigDecimal spend;

    @ExcelProperty("利润(元)")
    private BigDecimal income;

}
