package cn.iocoder.yudao.module.domain.controller.admin.domain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - DSP域名管理新增/修改 Request VO")
@Data
public class DomainSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "5105")
    private Long id;

    @Schema(description = "预算ID", example = "26191")
    private Long dspSlotId;

    @Schema(description = " 域名", example = "芋艿")
    private String domainName;

    @Schema(description = "所属公司", example = "李四")
    private String dspName;

}