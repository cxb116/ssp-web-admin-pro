package cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - DSP预算广告位小时报分页 Request VO")
@Data
public class DspSlotHourPageReqVO extends PageParam {

    @Schema(description = "预算位ID", example = "20637")
    private Long dspSlotId;

    @Schema(description = "预算广告位")
    private String dspSlotCode;

    @Schema(description = "SSP slot id", example = "26099")
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
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Integer[] date;

    @Schema(description = "创建时间戳")
    private Integer createdAt;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}