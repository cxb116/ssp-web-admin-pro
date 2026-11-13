package cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - DSP域名详情分页 Request VO")
@Data
public class DomainInfoPageReqVO extends SortablePageParam {

    @Schema(description = "预算ID", example = "23288")
    private Long dspSlotId;

    @Schema(description = "预算公司名称（按 dsp_company.name 精确匹配，过滤其下所有预算位）", example = "新多")
    private String dspCompanyName;

    @Schema(description = "媒体ID", example = "20859")
    private Long sspSlotId;

    @Schema(description = "域名集")
    private String domains;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}