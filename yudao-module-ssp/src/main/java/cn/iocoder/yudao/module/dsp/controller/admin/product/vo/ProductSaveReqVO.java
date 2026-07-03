package cn.iocoder.yudao.module.dsp.controller.admin.product.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 预算产品新增/修改 Request VO")
@Data
public class ProductSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "13801")
    private Long id;

    @Schema(description = "产品名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "李四")
    @NotEmpty(message = "产品名称不能为空")
    private String name;

    @Schema(description = "公司id", requiredMode = Schema.RequiredMode.REQUIRED, example = "14579")
    @NotNull(message = "公司id不能为空")
    private Long companyId;

}