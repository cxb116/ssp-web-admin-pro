package cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - DSP域名详情新增/修改 Request VO")
@Data
public class DomainInfoSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "10076")
    private Long id;

    @Schema(description = "预算ID", example = "23288")
    private Long dspSlotId;

    @Schema(description = "媒体ID", example = "20859")
    private Long sspSlotId;

    @Schema(description = "域名集")
    private String domains;

}