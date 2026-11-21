package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 黑名单统计分页 Request VO")
@Data
public class SlotBlacklistDomainPageReqVO extends PageParam {

    @Schema(description = "媒体id", example = "12682")
    private Long mediaId;

    @Schema(description = "拦截域名")
    private String domain;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}