package cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import java.time.LocalDateTime;

/**
 * 捕获日志 VO（内存缓冲记录，不入库）
 */
@Schema(description = "管理后台 - 捕获日志 Response VO")
@Data
public class CaptureLogVO {

    @Schema(description = "序号（内存缓冲内递增，详情按此获取）", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    private Long seq;

    @Schema(description = "捕获类型（标准事件集）：原始请求/转发请求/响应体/请求超时/JSON转化失败/响应媒体数据 / unknown=未识别", example = "响应体")
    private String type;

    @Schema(description = "DSP名称", example = "DSP_ZhuiYueDsp")
    private String dspName;

    @Schema(description = "预算位ID", example = "826")
    private Long dspSlotId;

    @Schema(description = "流量位ID", example = "367168")
    private Long sspSlotId;

    @Schema(description = "报文是否被截断（dsp-api 侧超 512KB 截断）", example = "false")
    private Boolean truncated;

    @Schema(description = "报文总长度（字节，前端据此判定大报文置顶）", example = "20480")
    private Integer contentLength;

    /**
     * 列表接口只返回前若干字符预览（preview），完整报文走 get(seq) 获取
     */
    @Schema(description = "报文预览（列表接口返回，前 512 字符）")
    private String preview;

    @Schema(description = "报文全文（仅 get 接口返回）")
    private String content;

    @Schema(description = "捕获时间（入库内存时间）")
    private LocalDateTime createTime;

}
