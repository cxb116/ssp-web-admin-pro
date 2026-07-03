package cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - DSP预算广告位日期报分页 Request VO")
@Data
public class DspSlotDayPageReqVO extends PageParam {

    @Schema(description = "预算位ID", example = "7185")
    private Long dspSlotId;

    @Schema(description = "预算广告位ID")
    private String dspSlotCode;

    @Schema(description = "媒体广告ID", example = "9273")
    private Long sspSlotId;

}