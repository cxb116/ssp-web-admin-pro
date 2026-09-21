package cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;

/**
 * 小时报表折线图 Response VO
 *
 * 用于返回某一天（小时维度，0~23点）所有符合条件的广告位记录聚合后的各指标数据，
 * 供前端折线图每小时一个数据点使用。填充率 / 展现率 / 点击率 / ecpm 等为派生指标，
 * 在 Service 层实时计算返回。
 *
 * @author 芋道源码
 */
@Schema(description = "管理后台 - DSP-SSP小时报表折线图 Response VO")
@Data
@EqualsAndHashCode
@ToString
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SspSlotHourTrendRespVO {

    @Schema(description = "小时（0~23）")
    private Integer hour;

    @Schema(description = "请求数")
    private Long reqPv;

    @Schema(description = "返回PV")
    private Long retPv;

    @Schema(description = "展示PV")
    private Long showPv;

    @Schema(description = "点击PV")
    private Long clickPv;

    @Schema(description = "填充率（返回/请求*100%）")
    private Double fillRate;

    @Schema(description = "展现率（展示/返回*100%）")
    private Double displayRate;

    @Schema(description = "点击率（点击/展示*100%）")
    private Double clickRate;

    @Schema(description = "ecpm（预算千次展示收益）=收入/请求*1000")
    private Long ecpm;

    @Schema(description = "媒体ecpm（媒体千次展示收益）=成本/请求*1000")
    private Long mediaEcpm;

    @Schema(description = "ecprm（预算百万请求收益）=收入/请求*1000000")
    private Long ecprm;

    @Schema(description = "媒体ecprm（媒体百万请求收益）=成本/请求*1000000")
    private Long mediaEcprm;

    @Schema(description = "成本(分)")
    private Long spend;

    @Schema(description = "收入(分)")
    private Long income;
}