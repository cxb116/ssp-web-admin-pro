package cn.iocoder.yudao.module.dsp.controller.admin.product.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

import static cn.iocoder.yudao.framework.common.util.date.DateUtils.FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND;

@Schema(description = "管理后台 - 预算产品分页 Request VO")
@Data
public class ProductPageReqVO extends PageParam {

    @Schema(description = "产品名称", example = "李四")
    private String name;

    @Schema(description = "公司id", example = "14579")
    private Long companyId;

    @Schema(description = "操作系统类型 1=Android，2=iOS", example = "1")
    private Integer osType;

    @Schema(description = "创建时间")
    @DateTimeFormat(pattern = FORMAT_YEAR_MONTH_DAY_HOUR_MINUTE_SECOND)
    private LocalDateTime[] createTime;

}