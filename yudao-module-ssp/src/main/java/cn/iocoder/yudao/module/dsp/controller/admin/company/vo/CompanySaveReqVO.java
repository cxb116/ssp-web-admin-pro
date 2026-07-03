package cn.iocoder.yudao.module.dsp.controller.admin.company.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 预算广告新增/修改 Request VO")
@Data
public class CompanySaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32229")
    private Long id;

    @Schema(description = "公司名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "张三")
    @NotEmpty(message = "公司名称不能为空")
    private String name;

    @Schema(description = "预算映射值", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "预算映射值不能为空")
    private Long dspCode;

    @Schema(description = "请求地址", requiredMode = Schema.RequiredMode.REQUIRED, example = "https://www.iocoder.cn")
    @NotEmpty(message = "请求地址不能为空")
    private String url;

    @Schema(description = "请求方法")
    private Integer method;

    @Schema(description = "超时时间")
    private Integer timeout;

}