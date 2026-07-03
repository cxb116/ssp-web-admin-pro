package cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - DSP-SSP广告位报分页 Request VO")
@Data
public class SspSlotDayPageReqVO extends PageParam {

    @Schema(description = "媒体用户Id", example = "27526")
    private Long mediaId;

    @Schema(description = "应用ID", example = "11968")
    private Long appId;

    @Schema(description = "SSP广告位ID", example = "25423")
    private Long sspSlotId;

    @Schema(description = "DSP广告位ID", example = "32391")
    private Long dspSlotId;

    @Schema(description = "预算广告位编号")
    private String dspSlotCode;

}