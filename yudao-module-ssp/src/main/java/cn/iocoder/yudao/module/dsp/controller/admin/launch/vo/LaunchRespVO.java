package cn.iocoder.yudao.module.dsp.controller.admin.launch.vo;

import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.DspSlotInfoRespVO;
import cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo.SspSlotInfoRespVO;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 媒体预算绑定 Response VO")
@Data
@ExcelIgnoreUnannotated
public class LaunchRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24679")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "流量广告位Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "21260")
    @ExcelProperty("流量广告位Id")
    private Long sspSlotId;

    @Schema(description = "预算广告位id", requiredMode = Schema.RequiredMode.REQUIRED, example = "18245")
    @ExcelProperty("预算广告位id")
    private Long dspSlotId;

    @Schema(description = "流量权重（最大值100）", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流量权重（最大值100）")
    private Integer trafficWeight;

    @Schema(description = "流量组，流量1，流量2", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("流量组，流量1，流量2")
    private Integer trafficGroup;

    @Schema(description = "底价 (给上游媒体底价,预算是rtb的时候就需要这个值)", example = "17241")
    @ExcelProperty("底价 (给上游媒体底价,预算是rtb的时候就需要这个值)")
    private Long floorPrice;

    @Schema(description = "预算rtb时的分成系数")
    @ExcelProperty("预算rtb时的分成系数")
    private Integer dspPayRatio;

    @Schema(description = "投放时段 1全时段 2 自定义", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("投放时段 1全时段 2 自定义")
    private Integer launchTime;

    @Schema(description = "投放时间段的时间字符串")
    @ExcelProperty("投放时间段的时间字符串")
    private String launchHour;

    @Schema(description = "日志捕获时间戳")
    @ExcelProperty("日志捕获时间戳")
    private Long logTime;

    @Schema(description = "请求次数")
    @ExcelProperty("请求次数")
    private Integer req;

    @Schema(description = "展现次数")
    @ExcelProperty("展现次数")
    private Integer ims;

    @Schema(description = "点击次数")
    @ExcelProperty("点击次数")
    private Integer clk;

    @Schema(description = "包透传 1 透传 2 不透传")
    @ExcelProperty("包透传 1 透传 2 不透传")
    private Integer pkgTrans;

    @Schema(description = "预算广告位信息")
    private List<DspSlotInfoRespVO> dspSlotInfoDO;

    @Schema(description = "媒体广告位信息")
    private List<SspSlotInfoRespVO> sspSlotInfoDo;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}