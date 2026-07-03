package cn.iocoder.yudao.module.ssp.controller.admin.sspSlotInfo.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;

import javax.validation.constraints.*;

@Schema(description = "管理后台 - 媒体广告位修改 Request VO")
@Data
public class SspSlotInfoUpdateReqVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "4229")
    @NotNull(message = "ID不能为空")
    private Long id;

    @Schema(description = "媒体id", example = "2958")
    private Long mediaId;

    @Schema(description = "应用id", example = "19705")
    private Long appId;

    @Schema(description = "广告位名称", example = "芋艿")
    private String name;

    @Schema(description = "内部广告位名称")
    private String nameAlise;

    @Schema(description = "广告场景")
    private Long adScene;

    @Schema(description = "样式尺寸")
    private Long adSize;

    @Schema(description = "结算方式", example = "1")
    private Integer sspPayType;

    @Schema(description = "分成系数")
    private Integer sspDealRatio;

    @Schema(description = "固价", example = "22859")
    private Integer fixedPrice;

    @Schema(description = "广告位图片")
    private String adImage;

    @Schema(description = "广告位状态")
    private Integer enable;

}