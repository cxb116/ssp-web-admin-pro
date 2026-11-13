package cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - DSP域名公司列分页 Request VO")
@Data
public class DomainCompanyPageReqVO extends PageParam {

    @Schema(description = "预算ID", example = "30074")
    private Long dspCompanyId;

    @Schema(description = " 域名", example = "赵六")
    private String domainName;

    @Schema(description = "所属公司", example = "赵六")
    private String dspName;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}