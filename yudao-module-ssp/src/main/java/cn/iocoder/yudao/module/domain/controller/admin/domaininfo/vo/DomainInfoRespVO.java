package cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP域名详情 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DomainInfoRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10076")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "预算ID", example = "23288")
    @ExcelProperty("预算ID")
    private Long dspSlotId;

    @Schema(description = "预算位名称（后端关联 dsp_slot_info 带出，前端无需全量映射）", example = "全媒-帧间漫游")
    @ExcelProperty("预算位名称")
    private String dspSlotName;

    @Schema(description = "预算公司名称（后端关联 dsp_company 带出）", example = "全媒")
    @ExcelProperty("预算公司名称")
    private String dspCompanyName;

    @Schema(description = "媒体ID", example = "20859")
    @ExcelProperty("媒体ID")
    private Long sspSlotId;

    @Schema(description = "域名集", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("域名集")
    private String domains;

    @Schema(description = "该 dsp_slot_id 下各条记录中，单条 domains 域名数量最多的数量", example = "9")
    @ExcelProperty("最大域名数量")
    private Integer maxDomainLength;

    @Schema(description = "今日请求PV（data_dsp_slot_day 当天 req_pv 汇总）", example = "1024")
    @ExcelProperty("今日请求PV")
    private Long todayReqPv;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}