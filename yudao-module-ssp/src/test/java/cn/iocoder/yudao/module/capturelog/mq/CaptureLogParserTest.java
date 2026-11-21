package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 捕获日志解析器单测：标准模板六事件逐条验证 + 边界兜底。
 * 契约来源：dsp-api 端 impl/CaptureLog.go 文档（三段式标准模板）。
 */
class CaptureLogParserTest {

    @Test
    void test原始请求与转发请求() {
        CaptureLogVO vo = CaptureLogParser.parse(
                "{\"slotid\":768} ### QUANMEI 原始请求 ### 预算位ID: 768, 流量ID: 367110,");
        assertEquals("原始请求", vo.getType());
        assertEquals("QUANMEI", vo.getDspName());
        assertEquals(768L, vo.getDspSlotId());
        assertEquals(367110L, vo.getSspSlotId());
        assertEquals("{\"slotid\":768}", vo.getContent());

        vo = CaptureLogParser.parse(
                "{\"rid\":\"x\"} ### QUANMEI 转发请求 ### 预算位ID: 768, 流量ID: 367110,");
        assertEquals("转发请求", vo.getType());
        assertEquals("QUANMEI", vo.getDspName());
    }

    @Test
    void test响应体() {
        CaptureLogVO vo = CaptureLogParser.parse(
                "{\"code\":0} ### QUANMEI 响应体 ### 预算位ID: 768, 流量ID: 367110,");
        assertEquals("响应体", vo.getType());
        assertEquals("QUANMEI", vo.getDspName());
        // '响应体' 不是 '响应媒体数据' 的后缀，二者不可误匹配
        vo = CaptureLogParser.parse("{...} ### HUANXI 响应媒体数据 ### 预算位ID: 1, 流量ID: 2,");
        assertEquals("响应媒体数据", vo.getType());
        assertEquals("HUANXI", vo.getDspName());
    }

    @Test
    void test请求超时占位与JSON转化失败() {
        // 超时行：报文段为 timeout 占位，保持三段式
        CaptureLogVO vo = CaptureLogParser.parse(
                "timeout ### QUANMEI 请求超时 ### 预算位ID: 768, 流量ID: 367110,");
        assertEquals("请求超时", vo.getType());
        assertEquals("QUANMEI", vo.getDspName());
        assertEquals("timeout", vo.getContent());

        // 解析失败行：err 文本当报文段
        vo = CaptureLogParser.parse(
                "unexpected token ### QUANMEI JSON转化失败 ### 预算位ID: 768, 流量ID: 367110,");
        assertEquals("JSON转化失败", vo.getType());
        assertEquals("unexpected token", vo.getContent());
    }

    @Test
    void test截断标记与无DSP名边界() {
        // 截断行：行尾 (truncated)，尾部元信息后缀必须保留可解析
        CaptureLogVO vo = CaptureLogParser.parse(
                "aaaa ### QUANMEI 响应体 ### 预算位ID: 768, 流量ID: 367110,(truncated)");
        assertEquals("响应体", vo.getType());
        assertEquals(768L, vo.getDspSlotId());
        assertTrue(vo.getTruncated());

        // 事件段无 DSP 名（历史格式防御）：head 即事件本身，dspName 为空不崩
        vo = CaptureLogParser.parse("{...} ### 响应媒体数据 ### 预算位ID: 1, 流量ID: 2,");
        assertEquals("响应媒体数据", vo.getType());
        assertEquals("", vo.getDspName());
    }

    @Test
    void test毒消息兜底() {
        // 无分隔符 → unknown 原文保留
        CaptureLogVO vo = CaptureLogParser.parse("乱七八糟没有分隔符的行");
        assertEquals(CaptureLogParser.TYPE_UNKNOWN, vo.getType());
        assertEquals("乱七八糟没有分隔符的行", vo.getContent());
        assertEquals(0L, vo.getDspSlotId());

        // 空串 / null 不抛异常
        assertEquals(CaptureLogParser.TYPE_UNKNOWN, CaptureLogParser.parse("").getType());
        assertEquals(CaptureLogParser.TYPE_UNKNOWN, CaptureLogParser.parse(null).getType());

        // 维度段存在但无数字 → 事件可识别、维度为 0
        vo = CaptureLogParser.parse("x ### QUANMEI 响应体 ### 预算位ID: , 流量ID: ,");
        assertEquals("响应体", vo.getType());
        assertEquals("QUANMEI", vo.getDspName());
        assertEquals(0L, vo.getDspSlotId());
        assertEquals(0L, vo.getSspSlotId());
    }

}
