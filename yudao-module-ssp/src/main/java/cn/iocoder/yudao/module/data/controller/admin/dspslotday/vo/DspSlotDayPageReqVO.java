package cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo;

import lombok.*;
import java.util.*;
import io.swagger.v3.oas.annotations.media.Schema;
import cn.iocoder.yudao.framework.common.pojo.SortablePageParam;

@Schema(description = "管理后台 - DSP预算广告位日期报分页 Request VO")
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
public class DspSlotDayPageReqVO extends SortablePageParam {

    @Schema(description = "预算位ID", example = "7185")
    private Long[] dspSlotId;

    @Schema(description = "预算广告位ID")
    private String dspSlotCode;

    @Schema(description = "预算广告位ID列表")
    private List<String> dspSlotCodes;

    @Schema(description = "媒体广告ID", example = "9273")
    private Long[] sspSlotId;

    @Schema(description = "时间范围，格式 ['YYYY-MM-DD', 'YYYY-MM-DD']")
    private List<String> date;

    @Schema(description = "操作系统")
    private Integer osType;

    @Schema(description = "预算公司ID")
    private Long companyId;

    @Schema(description = "预算产品ID")
    private Long[] productId;


}