package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class DspSlotDayInputRespVo {


    private Long id;

    @Schema(description = "时间")
    private Integer date;

    @Schema(description = "DSP预算名称")
    private String dspName;

    @Schema(description = "预算广告位ID", requiredMode = Schema.RequiredMode.REQUIRED)
    private String dspSlotCode;

    @Schema(description = "预算位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7185")
    private Long dspSlotId;

    @Schema(description = "公司名称")
    private String companyName;

    @Schema(description = "产品名称")

    private String productName;

    @Schema(description = "媒体广告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9273")
    private Long sspSlotId;

    @Schema(description = "媒体名称")
    private String mediaName;

    @Schema(description = "媒体广告位名称")
    private String sspName;

    @Schema(description = "应用名称")
    private String appName;

    @Schema(description = "操作系统 1=Android 2=iOS")
    private Integer osType;

    @Schema(description = "请求PV")
    private Long reqPv;

    @Schema(description = "请求UV")
    private Long reqUv;

    @Schema(description = "丢弃请求")
    private Long discard;


    @Schema(description = "返回PV")
    private Long retPv;

    @Schema(description = "返回UV")
    private Long retUv;

    @Schema(description = "展示PV")
    private Long showPv;

    @Schema(description = "展示UV")
    private Long showUv;

    @Schema(description = "点击PV")
    private Long clickPv;

    @Schema(description = "点击UV")
    private Long clickUv;

    @Schema(description = "填充率")
    private Double fillRate;

    @Schema(description = "展现率")
    private Double displayRate;

    @Schema(description = "点击率")
    private Double clickRate;

    @Schema(description = "折后点击")
    private Long discountClickPv;

    @Schema(description = "折后展示")
    private Long discountShowPv;

    @Schema(description = "调起成功")
    private Long dplsuccPv;

    @Schema(description = "完成量")
    private Long completePv;

    @Schema(description = "安装量")
    private Long installPv;

    @Schema(description = "激活量")
    private Long activatePv;

    @Schema(description = "创建时间戳")
    private Integer createdAt;


    @Schema(description = "媒体ecpm（媒体千次展示收益）")
    private Double mediaEcpm;

    @Schema(description = "ecpm（预算千次展示收益）")
    private Double ecpm;

    @Schema(description = "媒体ecprm（媒体百万请求收益）")
    private Double mediaEcprm;

    @Schema(description = "ecprm（预算百万请求收益）")
    private Double ecprm;

    @Schema(description = "成本(分)")
    private BigDecimal spend;

    @Schema(description = "收入(分)")
    private BigDecimal income;

    @Schema(description = "收益(分)")
    private BigDecimal profit;

    @Schema(description = "分成比例,默认比例80")
    private int  proportion;



}
