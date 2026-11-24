package cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo;

import cn.idev.excel.annotation.ExcelProperty;
import cn.idev.excel.annotation.ExcelIgnore;
import lombok.Data;
import java.math.BigDecimal;

@Data
public class SspSlotHourExportVO {
    @ExcelProperty("时间")
    private String hour;

    @ExcelProperty("媒体简称")
    private String mediaName;

    @ExcelProperty("媒体广告位名称")
    private String sspName;

    @ExcelProperty("媒体广告位ID")
    private Long sspSlotId;

    @ExcelProperty("应用名称")
    private String appName;

    @ExcelProperty("操作系统")
    private String osLabel;

    @ExcelProperty("请求PV")
    private Long reqPv;

    @ExcelProperty("丢弃请求")
    private Long discard;

//    @ExcelProperty("丢弃响应")
    @ExcelIgnore
    private Long retDiscard;

    @ExcelProperty("返回PV")
    private Long retPv;

    @ExcelProperty("展示PV")
    private Long showPv;

    @ExcelProperty("点击PV")
    private Long clickPv;

    @ExcelProperty("填充率")
    private String fillRateText;

    @ExcelProperty("展现率")
    private String displayRateText;

    @ExcelProperty("点击率")
    private String clickRateText;

//    @ExcelProperty("折后点击")
    private Long discountClickPv;

//    @ExcelProperty("折后展示")
    private Long discountShowPv;

//    @ExcelProperty("调起成功")
    @ExcelIgnore
    private Long dplsuccPv;

//    @ExcelProperty("完成量")
    @ExcelIgnore
    private Long completePv;

//    @ExcelProperty("安装量")
    @ExcelIgnore
    private Long installPv;

//    @ExcelProperty("激活量")
    @ExcelIgnore
    private Long activatePv;

    @ExcelProperty("媒体ecpm")
    private BigDecimal mediaEcpmValue;

    @ExcelProperty("ecpm")
    private BigDecimal ecpmValue;

    @ExcelProperty("媒体ecprm")
    private BigDecimal mediaEcprmValue;

    @ExcelProperty("ecprm")
    private BigDecimal ecprmValue;

    @ExcelProperty("收益(元)")
    private BigDecimal revenue;

    @ExcelProperty("成本(元)")
    private BigDecimal spendValue;

    @ExcelProperty("利润(元)")
    private BigDecimal incomeValue;

}
