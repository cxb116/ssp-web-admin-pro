package cn.iocoder.yudao.module.ssp.controller.admin.media.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体分页 Request VO")
@Data
public class MediaPageReqVO extends PageParam {

    @Schema(description = "账号名", example = "7880")
    private String account;

    @Schema(description = "公司简称")
    private String mediaCompanyShort;

    @Schema(description = "接入方式", example = "1")
    private Integer accessType;

    @Schema(description = "流量类型", example = "2")
    private Integer trafficType;

    @Schema(description = "媒体状态")
    private Integer enable;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] updateTime;

}