package cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 媒体广告位分页 Request VO")
@Data
public class SspSlotInfoPageReqVO extends PageParam {

    @Schema(description = "ID", example = "1")
    private List<Long> id;

    @Schema(description = "媒体id", example = "2958")
    private Long mediaId;

    @Schema(description = "应用id", example = "19705")
    private List<Long> appId;

    @Schema(description = "广告位名称", example = "芋艿")
    private List<String> name;

    @Schema(description = "内部广告位名称")
    private List<String> nameAlise;

    @Schema(description = "广告场景")
    private Long adScene;

    @Schema(description = "操作系统")
    private Long osType;


    @Schema(description = "结算方式", example = "1")
    private Integer sspPayType;

    @Schema(description = "广告位状态")
    private Integer enable;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] updateTime;

}