package cn.iocoder.yudao.module.ssp.controller.admin.media.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 媒体新增/修改 Request VO")
@Data
public class MediaSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "803")
    private Long id;

    @Schema(description = "账号名", requiredMode = Schema.RequiredMode.REQUIRED, example = "7880")
    @NotEmpty(message = "账号名不能为空")
    private String account;

    @Schema(description = "密码", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "密码不能为空")
    private String password;

    @Schema(description = "公司简称", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "公司简称不能为空")
    private String mediaCompanyShort;

    @Schema(description = "媒体名称", example = "芋艿")
    private String name;

    @Schema(description = "公司名称", example = "李四")
    private String mediaCompanyName;

    @Schema(description = "统一社会信用代码")
    private String mediaCompanyCode;

    @Schema(description = "营业执照照片")
    private String mediaCompanyLicense;

    @Schema(description = "公司地址")
    private String mediaCompanyAddress;

    @Schema(description = "法人姓名", example = "张三")
    private String mediaOwnerName;

    @Schema(description = "联系人", example = "张三")
    private String contactName;

    @Schema(description = "联系电话")
    private String contactPhone;

    @Schema(description = "联系邮箱")
    private String contactEmail;

    @Schema(description = "接入方式", example = "1")
    private Integer accessType;

    @Schema(description = "流量类型", example = "2")
    private Integer sspType;

    @Schema(description = "媒体状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "媒体状态不能为空")
    private Integer enable;

}