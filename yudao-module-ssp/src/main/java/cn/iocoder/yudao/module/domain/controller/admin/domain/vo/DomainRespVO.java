package cn.iocoder.yudao.module.domain.controller.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP域名管理 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DomainRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5105")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "预算ID", example = "26191")
    @ExcelProperty("预算ID")
    private Long dspSlotId;

    @Schema(description = " 域名", example = "芋艿")
    @ExcelProperty(" 域名")
    private String domainName;

    @Schema(description = "所属公司", example = "李四")
    @ExcelProperty("所属公司")
    private String dspName;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}