package cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - DSP域名公司列 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DomainCompanyRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14749")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "预算ID", example = "30074")
    @ExcelProperty("预算ID")
    private Long dspCompanyId;

    @Schema(description = " 域名", example = "赵六")
    @ExcelProperty(" 域名")
    private String domainName;

    @Schema(description = "所属公司", example = "赵六")
    @ExcelProperty("所属公司")
    private String dspName;

    @Schema(description = "备注", example = "备注信息")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}