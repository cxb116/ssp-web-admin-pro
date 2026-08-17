package cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo;

import lombok.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;

@Schema(description = "管理后台 - DSP预算广告位小时报分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DspSlotHourPageReqVO extends SortablePageParam {

    @Schema(description = "公司ID", example = "1")
    private Long companyId;

    @Schema(description = "应用平台", example = "1")
    private Integer osType;

    @Schema(description = "产品ID", example = "1")
    private Integer[] productId;

    @Schema(description = "预算位ID", example = "20637")
    private Long[] dspSlotId;

    @Schema(description = "预算广告位")
    private String dspSlotCode;

    @Schema(description = "预算广告位ID列表")
    private String[] dspSlotCodes;

    @Schema(description = "SSP slot id", example = "26099")
    private Long[] sspSlotId;

    @Schema(description = "时间(yyyyMMddHH)")
    private Integer[] date;

}