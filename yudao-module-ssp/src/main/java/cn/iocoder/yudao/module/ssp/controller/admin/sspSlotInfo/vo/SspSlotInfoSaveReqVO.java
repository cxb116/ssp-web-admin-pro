package cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import javax.validation.constraints.*;

@Schema(description = "管理后台 - 媒体广告位新增/修改 Request VO")
@Data
public class SspSlotInfoSaveReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4229")
    private Long id;

    @Schema(description = "媒体id", requiredMode = Schema.RequiredMode.REQUIRED, example = "2958")
    @NotNull(message = "媒体id不能为空")
    private Long mediaId;

    @Schema(description = "应用id", requiredMode = Schema.RequiredMode.REQUIRED, example = "19705")
    @NotNull(message = "应用id不能为空")
    private Long appId;

    @Schema(description = "广告位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "芋艿")
    @NotEmpty(message = "广告位名称不能为空")
    private String name;

    @Schema(description = "内部广告位名称")
    private String nameAlise;

    @Schema(description = "广告场景", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告场景不能为空")
    private Long adScene;

    @Schema(description = "样式尺寸")
    private Long adSize;

    @Schema(description = "结算方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @NotNull(message = "结算方式不能为空")
    private Integer sspPayType;

    @Schema(description = "分成系数")
    private Integer sspDealRatio;

    @Schema(description = "固价", example = "22859")
    private Integer fixedPrice;

    @Schema(description = "广告位图片")
    private String adImage;

    @Schema(description = "广告位状态", requiredMode = Schema.RequiredMode.REQUIRED)
    @NotNull(message = "广告位状态不能为空")
    private Integer enable;

}