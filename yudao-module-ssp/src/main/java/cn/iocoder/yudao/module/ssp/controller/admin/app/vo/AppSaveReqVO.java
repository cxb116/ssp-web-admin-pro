package cn.iocoder.yudao.module.ssp.controller.admin.app.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 媒体应用新增/修改 Request VO")
@Data
public class AppSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "17508")
    private Long id;

    @Schema(description = "媒体Id", requiredMode = Schema.RequiredMode.REQUIRED, example = "5471")
    @NotNull(message = "媒体Id不能为空")
    private Long mediaId;

    @Schema(description = "应用名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "应用名称不能为空")
    private String name;

    @Schema(description = "操作系统", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "操作系统不能为空")
    private Integer osType;

    @Schema(description = "接入方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "接入方式不能为空")
    private Integer accessType;

    @Schema(description = "包名", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "包名不能为空")
    private String pkg;

    @Schema(description = "下载地址", example = "https://www.iocoder.cn")
    private String downloadUrl;

    @Schema(description = "应用状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "应用状态不能为空")
    private Integer enable;

}