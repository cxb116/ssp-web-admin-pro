package cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import java.time.LocalDate;

@Schema(description = "管理后台 - 当日域名占比分析 Response VO")
@Data
public class DomainInfoProportionAnalysisRespVO {

    @Schema(description = "统计日期")
    private LocalDate statDate;

    @Schema(description = "预算位 ID")
    private Long dspSlotId;

    @Schema(description = "预算位名称")
    private String dspSlotName;

    @Schema(description = "预算公司名称")
    private String dspCompanyName;

    @Schema(description = "去重后的域名组")
    private String domains;

    @Schema(description = "该预算位当天所有域名组次数")
    private Long groupCount;

    @Schema(description = "全站当天所有域名组次数")
    private Long totalCount;

    @Schema(description = "该预算位当天所有域名组次数")
    private Long budgetTotalCount;

    @Schema(description = "该预算位当天次数占全站当天总次数的比例，百万分比")
    private Long proportion;

    @Schema(description = "按占比计算的名次")
    private Integer proportionRank;

    @Schema(description = "按次数计算的名次")
    private Integer countRank;
}
