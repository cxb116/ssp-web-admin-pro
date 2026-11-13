package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;
import lombok.extern.slf4j.Slf4j;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.List;

/**
 * 捕获日志 Kafka 消费者
 *
 * 消费 dsp-api 各捕获点经 topic log-data 上报的日志原文，解析后写入内存缓冲
 * {@link CaptureLogBuffer}（不落库，只用于线上问题排查），管理后台通过
 * /capture/log 接口查询最近日志。
 *
 * 消费语义：
 *  - 批量监听（listener.type=batch），逐条解析写入缓冲；
 *  - 单条解析异常兜底 unknown 原文保留，绝不抛出 —— 毒消息不会阻断分区消费；
 *  - 缓冲写满自动淘汰最旧记录（历史仍在 Kafka，topic 保留 3 天可重放）。
 */
@Slf4j
@Component
public class CaptureLogConsumer {

    @Resource
    private CaptureLogBuffer captureLogBuffer;

    @KafkaListener(
            topics = "${ssp.capture-log.topic:log-data}",
            groupId = "${spring.kafka.consumer.group-id:ssp-capture-log-group}",
            containerFactory = "captureLogBatchFactory" // SB2.7 无 listener.type=batch，批量工厂见 CaptureLogKafkaConfig
    )
    public void onMessage(List<ConsumerRecord<String, String>> records) {
        if (records == null || records.isEmpty()) {
            return;
        }
        for (ConsumerRecord<String, String> record : records) {
            try {
                CaptureLogVO vo = CaptureLogParser.parse(record.value());
                captureLogBuffer.add(vo);
            } catch (Exception e) {
                // 兜底：任何异常都以 unknown 原文保留，不能让毒消息阻断消费
                log.error("[onMessage][捕获日志处理异常 offset={}]", record.offset(), e);
                try {
                    CaptureLogVO fallback = new CaptureLogVO();
                    fallback.setType(CaptureLogParser.TYPE_UNKNOWN);
                    fallback.setContent(record.value());
                    captureLogBuffer.add(fallback);
                } catch (Exception ignore) {
                    // 缓冲写入本身失败（理论上不可能），丢弃该条，绝不上抛
                }
            }
        }
    }

}
