package cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - DSP域名公司列新增/修改 Request VO")
@Data
public class DomainCompanySaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "14749")
    private Long id;

    @Schema(description = "预算公司ID", example = "30074")
    private Long dspCompanyId;

    @Schema(description = " 域名", example = "赵六")
    private String domainName;

    @Schema(description = "所属公司", example = "赵六")
    private String dspName;

    @Schema(description = "备注", example = "备注信息")
    private String remark;

}