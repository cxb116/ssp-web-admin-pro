package cn.iocoder.yudao.module.dsp.controller.admin.launch.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体预算绑定分页 Request VO")
@Data
public class LaunchPageReqVO extends PageParam {

    @Schema(description = "流量广告位Id", example = "21260")
    private Long sspSlotId;

    @Schema(description = "预算广告位id", example = "18245")
    private Long dspSlotId;

    @Schema(description = "流量权重（最大值100）")
    private Integer trafficWeight;

    @Schema(description = "流量组，流量1，流量2")
    private Integer trafficGroup;

    @Schema(description = "底价 (给上游媒体底价,预算是rtb的时候就需要这个值)", example = "17241")
    private Long floorPrice;

    @Schema(description = "预算rtb时的分成系数")
    private Integer dspPayRatio;

    @Schema(description = "投放时段 1全时段 2 自定义")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Integer[] launchTime;

    @Schema(description = "投放时间段的时间字符串")
    private String launchHour;

    @Schema(description = "日志捕获时间戳")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private Long[] logTime;

    @Schema(description = "请求次数")
    private Integer req;

    @Schema(description = "展现次数")
    private Integer ims;

    @Schema(description = "点击次数")
    private Integer clk;

    @Schema(description = "包透传 1 透传 2 不透传")
    private Integer pkgTrans;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}