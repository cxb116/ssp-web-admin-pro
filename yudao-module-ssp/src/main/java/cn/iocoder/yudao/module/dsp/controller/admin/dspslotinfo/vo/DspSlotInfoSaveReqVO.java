package cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 预算广告位新增/修改 Request VO")
@Data
public class DspSlotInfoSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32243")
    private Long id;

    @Schema(description = "预算方广告位", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "预算方广告位不能为空")
    private String dspSlotCode;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31959")
    @NotNull(message = "产品ID不能为空")
    private Long productId;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16821")
    @NotNull(message = "公司ID不能为空")
    private Long companyId;

    @Schema(description = "广告位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @NotEmpty(message = "广告位名称不能为空")
    private String name;

    @Schema(description = "操作系统", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @NotNull(message = "操作系统不能为空")
    private Integer osType;

    @Schema(description = "结算方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "结算方式不能为空")
    private Integer dspPayType;

    @Schema(description = "广告场景", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告场景不能为空")
    private Long adScene;

    @Schema(description = "预算方APPKEY")
    private String dspAppKey;

    @Schema(description = "预算方APPSECRET")
    private String dspAppSecret;

    @Schema(description = "预算方APPID", example = "30241")
    private String dspAppId;

    @Schema(description = "预算方应用包名")
    private String dspAppPkg;

    @Schema(description = "应用版本号")
    private String dspAppVer;

    @Schema(description = "应用商店版本号")
    private String dspAppStoreVer;

    @Schema(description = "价格加密KEY")
    private String priceEncryptKey;

    @Schema(description = "应用商店地址")
    private String dspAppStoreLink;

}