package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 黑名单配置新增/修改 Request VO")
@Data
public class SlotBlacklistSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11554")
    private Long id;

    @Schema(description = "媒体ID", example = "6658")
    private Long mediaId;

    @Schema(description = "域名列表")
    private String backlistDomains;

    @Schema(description = "备注", example = "随便")
    private String remark;

    @Schema(description = "是否开启： 1开启，2关闭", example = "1")
    private String status;

}