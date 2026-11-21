package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 黑名单统计 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SlotBlacklistDomainRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24180")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "媒体id", example = "12682")
    @ExcelProperty("媒体id")
    private Long mediaId;

    @Schema(description = "拦截域名")
    @ExcelProperty("拦截域名")
    private String domain;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}