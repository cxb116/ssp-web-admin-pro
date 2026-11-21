package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 黑名单统计新增/修改 Request VO")
@Data
public class SlotBlacklistDomainSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "24180")
    private Long id;

    @Schema(description = "媒体id", example = "12682")
    private Long mediaId;

    @Schema(description = "拦截域名")
    private String domain;

}