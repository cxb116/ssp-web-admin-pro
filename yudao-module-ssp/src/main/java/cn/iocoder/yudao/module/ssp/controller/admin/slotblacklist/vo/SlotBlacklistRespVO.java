package cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;

@Schema(description = "管理后台 - 黑名单配置 Response VO")
@Data
@ExcelIgnoreUnannotated
public class SlotBlacklistRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "11554")
    @ExcelProperty("ID")
    private Long id;

    @Schema(description = "媒体ID", example = "6658")
    @ExcelProperty("媒体ID")
    private Long mediaId;

    @Schema(description = "域名列表")
    @ExcelProperty("域名列表")
    private String backlistDomains;

    @Schema(description = "备注", example = "随便")
    @ExcelProperty("备注")
    private String remark;

    @Schema(description = "是否开启： 1开启，2关闭", example = "1")
    @ExcelProperty("是否开启")
    private String status;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

}