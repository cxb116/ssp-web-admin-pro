package cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - DSP预算广告位日期报新增/修改 Request VO")
@Data
public class DspSlotDaySaveReqVO {

    @Schema(description = "主键", requiredMode = Schema.RequiredMode.REQUIRED, example = "25594")
    private Long id;

    @Schema(description = "预算位ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "7185")
    @NotNull(message = "预算位ID不能为空")
    private Long dspSlotId;

    @Schema(description = "预算广告位ID", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "预算广告位ID不能为空")
    private String dspSlotCode;

    @Schema(description = "媒体广告ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "9273")
    @NotNull(message = "媒体广告ID不能为空")
    private Long sspSlotId;

    @Schema(description = "展示PV")
    private Long showPv;

    @Schema(description = "展示UV")
    private Long showUv;

    @Schema(description = "点击PV")
    private Long clickPv;

    @Schema(description = "点击UV")
    private Long clickUv;

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

    @Schema(description = "成本(分)")
    private Long spend;

    @Schema(description = "收入(分)")
    private Long income;

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

    @Schema(description = "时间(yyyyMMdd / yyyyMMddHH)")
    private Integer date;

    @Schema(description = "创建时间戳")
    private Integer createdAt;

}