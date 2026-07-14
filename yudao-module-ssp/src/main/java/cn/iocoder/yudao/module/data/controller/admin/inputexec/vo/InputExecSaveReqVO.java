package cn.iocoder.yudao.module.data.controller.admin.inputexec.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;

@Schema(description = "管理后台 - DSP数据导入新增/修改 Request VO")
@Data
public class InputExecSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "23801")
    private Long id;

    @Schema(description = "预算公司id", requiredMode = Schema.RequiredMode.REQUIRED, example = "4207")
    @NotNull(message = "预算公司id不能为空")
    private Long companyId;

    @Schema(description = "导入条数")
    private Long tables;

    @Schema(description = "导入时间")
    @JsonFormat(pattern = "yyyy-MM-dd")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDateTime inputTime;

}