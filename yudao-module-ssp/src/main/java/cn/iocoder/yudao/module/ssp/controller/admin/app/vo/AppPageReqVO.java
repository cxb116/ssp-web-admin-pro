package cn.iocoder.yudao.module.ssp.controller.admin.app.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体应用分页 Request VO")
@Data
public class AppPageReqVO extends PageParam {

    @Schema(description = "媒体Id", example = "5471")
    private Long mediaId;

    @Schema(description = "应用名称", example = "芋艿")
    private String name;

    @Schema(description = "操作系统", example = "1")
    private Integer osType;

    @Schema(description = "接入方式", example = "1")
    private Integer accessType;

    @Schema(description = "应用状态")
    private Integer enable;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}