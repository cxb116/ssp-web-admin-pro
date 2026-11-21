package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 捕获日志原文解析器
 *
 * dsp-api 上报的标准模板（三段式，以 " ### " 分隔）：
 *
 *   {报文内容} ### {DSP名} {事件} ### 预算位ID: %d, 流量ID: %d,
 *
 * 标准事件集（dsp-api 端 impl/CaptureLog.go 文档为契约，新适配器照抄）：
 *   原始请求      规范化 BidRequest JSON（报文段）
 *   转发请求      发往 DSP 的协议请求体
 *   响应体        DSP 原始协议响应体
 *   请求超时      报文段为 "timeout" 占位（保持三段式）
 *   JSON转化失败  报文段为 err 文本
 *   响应媒体数据  竞价赢家下发给媒体的最终响应（含聚合后的 imp/click），事件段含 DSP 名
 *
 * 示例：
 *   {...} ### QUANMEI 转发请求 ### 预算位ID: 768, 流量ID: 367110,
 *   timeout ### QUANMEI 请求超时 ### 预算位ID: 768, 流量ID: 367110,
 *   {...} ### HUANXI 响应媒体数据 ### 预算位ID: 768, 流量ID: 367110,
 *
 * dsp-api 侧大报文（>512KB）截断时保留元信息后缀、行尾追加 "(truncated)"。
 * 解析规则与 dsp-api 端截断逻辑对齐：以第一个 " ### " 作为报文与元信息的分界。
 */
public class CaptureLogParser {

    static final String SEPARATOR = " ### ";
    static final String TRUNCATED_MARK = "(truncated)";
    /**
     * 标准事件集（endsWith 匹配，匹配到的后缀去掉即 DSP 名）。
     * 六个事件互不为彼此后缀，任意顺序匹配均安全。
     */
    private static final String[] TYPE_SUFFIXES = {
            "原始请求", "转发请求", "响应体", "请求超时", "JSON转化失败", "响应媒体数据"
    };
    private static final Pattern DSP_SLOT_PATTERN = Pattern.compile("预算位ID[:：]\\s*(\\d+)");
    private static final Pattern SSP_SLOT_PATTERN = Pattern.compile("流量ID[:：]\\s*(\\d+)");

    /** 未识别格式的兜底类型 */
    public static final String TYPE_UNKNOWN = "unknown";

    /**
     * 解析一条捕获日志原文；任何格式异常都不抛出，兜底为 unknown + 原文保留，避免毒消息阻断消费。
     */
    public static CaptureLogVO parse(String line) {
        CaptureLogVO vo = new CaptureLogVO();
        vo.setDspSlotId(0L); // 兜底维度统一为 0（避免 null 到前端）
        vo.setSspSlotId(0L);
        if (line == null || line.isEmpty()) {
            vo.setType(TYPE_UNKNOWN);
            vo.setContent("");
            return vo;
        }

        // 剥离行尾截断标记
        boolean truncated = false;
        String body = line;
        if (body.endsWith(TRUNCATED_MARK)) {
            truncated = true;
            body = body.substring(0, body.length() - TRUNCATED_MARK.length());
        }

        // 第一个 " ### " 之前是报文，之后是元信息
        int sep = body.indexOf(SEPARATOR);
        if (sep < 0) {
            vo.setType(TYPE_UNKNOWN);
            vo.setTruncated(truncated);
            vo.setContent(line);
            return vo;
        }
        String content = body.substring(0, sep);
        String meta = body.substring(sep + SEPARATOR.length());
        String[] segments = meta.split(SEPARATOR);

        // 段1："{DSP名} {事件}" → 事件 + DSP名（type 直接存事件名，前端按事件过滤/着色）
        String type = TYPE_UNKNOWN;
        String dspName = "";
        if (segments.length > 0) {
            String head = segments[0].trim();
            for (String suffix : TYPE_SUFFIXES) {
                if (head.endsWith(suffix)) {
                    type = suffix;
                    dspName = head.substring(0, head.length() - suffix.length()).trim();
                    break;
                }
            }
        }

        // 段2："预算位ID: 826, 流量ID: 367168," → 维度
        Long dspSlotId = 0L;
        Long sspSlotId = 0L;
        if (segments.length > 1) {
            Matcher m = DSP_SLOT_PATTERN.matcher(segments[1]);
            if (m.find()) {
                dspSlotId = Long.parseLong(m.group(1));
            }
            Matcher m2 = SSP_SLOT_PATTERN.matcher(segments[1]);
            if (m2.find()) {
                sspSlotId = Long.parseLong(m2.group(1));
            }
        }

        vo.setType(type);
        vo.setDspName(dspName);
        vo.setDspSlotId(dspSlotId);
        vo.setSspSlotId(sspSlotId);
        vo.setContent(content);
        vo.setTruncated(truncated);
        return vo;
    }

}
