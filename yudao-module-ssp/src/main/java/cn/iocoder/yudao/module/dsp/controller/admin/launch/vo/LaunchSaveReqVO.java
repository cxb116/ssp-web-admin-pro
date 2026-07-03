package cn.iocoder.yudao.module.dsp.controller.admin.launch.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 媒体预算绑定新增/修改 Request VO")
@Data
public class LaunchSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24679")
    private Long id;

    @Schema(description = "流量广告位Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21260")
    @NotNull(message = "流量广告位Id不能为空")
    private Long sspSlotId;

    @Schema(description = "预算广告位id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18245")
    @NotNull(message = "预算广告位id不能为空")
    private Long dspSlotId;

    @Schema(description = "流量权重（最大值100）", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "流量权重（最大值100）不能为空")
    private Integer trafficWeight;

    @Schema(description = "流量组，流量1，流量2", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "流量组，流量1，流量2不能为空")
    private Integer trafficGroup;

    @Schema(description = "底价 (给上游媒体底价,预算是rtb的时候就需要这个值)", example = "17241")
    private Long floorPrice;

    @Schema(description = "预算rtb时的分成系数")
    private Integer dspPayRatio;

    @Schema(description = "投放时段 1全时段 2 自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "投放时段 1全时段 2 自定义不能为空")
    private Integer launchTime;

    @Schema(description = "投放时间段的时间字符串")
    private String launchHour;

    @Schema(description = "日志捕获时间戳")
    private Long logTime;

    @Schema(description = "请求次数")
    private Integer req;

    @Schema(description = "展现次数")
    private Integer ims;

    @Schema(description = "点击次数")
    private Integer clk;

    @Schema(description = "包透传 1 透传 2 不透传")
    private Integer pkgTrans;

}