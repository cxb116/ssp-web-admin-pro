package cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 预算广告位分页 Request VO")
@Data
public class DspSlotInfoPageReqVO extends PageParam {

    @Schema(description = "预算方广告位")
    private String dspSlotCode;

    @Schema(description = "产品ID", example = "31959")
    private Long productId;

    @Schema(description = "公司ID", example = "16821")
    private Long companyId;

    @Schema(description = "广告位名称", example = "赵六")
    private String name;

    @Schema(description = "操作系统", example = "2")
    private Integer osType;

    @Schema(description = "结算方式", example = "1")
    private Integer dspPayType;

    @Schema(description = "广告场景")
    private Long adScene;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

    @Schema(description = "更新时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] updateTime;

}