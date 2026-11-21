package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 黑名单配置分页 Request VO")
@Data
public class SlotBlacklistPageReqVO extends PageParam {

    @Schema(description = "媒体ID", example = "6658")
    private Long mediaId;

    @Schema(description = "域名列表")
    private String backlistDomains;

    @Schema(description = "是否开启： 1开启，2关闭", example = "1")
    private String status;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}