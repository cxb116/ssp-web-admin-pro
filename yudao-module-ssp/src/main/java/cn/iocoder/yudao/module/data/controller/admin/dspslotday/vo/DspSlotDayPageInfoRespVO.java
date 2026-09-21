package cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

/** 预算角度详情，包含预算、媒体及应用关联信息。 */
@Data
@Schema(description = "DSP预算广告位详情")
public class DspSlotDayPageInfoRespVO extends DspSlotDayRespVO {
    @Schema(description = "预算广告位编码")
    private String dspSlotCode;
    @Schema(description = "预算广告位名称")
    private String dspName;
    @Schema(description = "预算操作系统类型")
    private Integer dspOsType;
    @Schema(description = "预算公司名称")
    private String companyName;
    @Schema(description = "预算产品名称")
    private String productName;
    @Schema(description = "媒体广告位名称")
    private String sspSlotName;
    @Schema(description = "媒体内部广告位名称")
    private String sspName;
    @Schema(description = "媒体广告场景")
    private Long sspAdScene;
    @Schema(description = "预算广告场景")
    private Long dspAdScene;
    @Schema(description = "媒体简称")
    private String mediaName;
    @Schema(description = "应用名称")
    private String appName;
}
