package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 捕获日志原文解析器
 *
 * dsp-api 上报的日志行格式（与本地 BusinessLog 打印完全一致）：
 *
 *   {报文原文} ### {DSP名} {req|res|ssp} catch ### 预算位ID: %d, 流量ID: %d,
 *
 * 示例：
 *   {"code":10103,"msg":"无广告"} ### DSP_ZhuiYueDsp res catch ### 预算位ID: 826, 流量ID: 367168,
 *
 * dsp-api 侧大报文（>512KB）截断时保留元信息后缀、行尾追加 "(truncated)"。
 * 解析规则与 dsp-api 端截断逻辑对齐：以第一个 " ### " 作为报文与元信息的分界。
 */
public class CaptureLogParser {

    static final String SEPARATOR = " ### ";
    static final String TRUNCATED_MARK = "(truncated)";
    /** 元信息段中的类型后缀，如 " req catch" */
    private static final String[] TYPE_SUFFIXES = {"req catch", "res catch", "ssp catch"};
    private static final Pattern DSP_SLOT_PATTERN = Pattern.compile("预算位ID[:：]\\s*(\\d+)");
    private static final Pattern SSP_SLOT_PATTERN = Pattern.compile("流量ID[:：]\\s*(\\d+)");

    /** 未识别格式的兜底类型 */
    public static final String TYPE_UNKNOWN = "unknown";

    /**
     * 解析一条捕获日志原文；任何格式异常都不抛出，兜底为 unknown + 原文保留，避免毒消息阻断消费。
     */
    public static CaptureLogVO parse(String line) {
        CaptureLogVO vo = new CaptureLogVO();
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

        // 段1："{DSP名} {type} catch" → 类型 + DSP名
        String type = TYPE_UNKNOWN;
        String dspName = "";
        if (segments.length > 0) {
            String head = segments[0].trim();
            for (String suffix : TYPE_SUFFIXES) {
                if (head.endsWith(suffix)) {
                    type = suffix.substring(0, suffix.indexOf(' ')); // req / res / ssp
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
