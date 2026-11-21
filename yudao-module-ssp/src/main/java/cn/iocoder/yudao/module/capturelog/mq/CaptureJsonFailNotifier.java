package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.system.api.notify.NotifyMessageSendApi;
import cn.iocoder.yudao.module.system.api.notify.dto.NotifySendSingleToUserReqDTO;
import cn.iocoder.yudao.framework.tenant.core.util.TenantUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * JSON转化失败 聚合告警器
 *
 * dsp-api 侧 JSON转化失败为严重事件【常推】（不在捕获窗口内），Kafka 消费端收到后
 * 按 预算位ID:流量位ID 维度累计；每 1 分钟定时汇总一次，对每个有失败的维度调用
 * 站内信通知模块发送告警（失败次数 + 预算位ID + 流量位ID）。
 *
 * 通知配置：
 *  - 模板 code：capture_json_fail（需在 系统管理->站内信模板 建立，SQL 见 sql/capture_notify_template.sql）
 *  - 接收人：ssp.capture-notify.admin-user-ids，默认 1（超管），逗号分隔可配多人
 *
 * 可靠性：计数为内存聚合（重启清零，告警最多延迟一个窗口，可接受）；
 *        单维度发送失败仅记日志，不影响其他维度与消费线程。
 */
@Slf4j
@Component
public class CaptureJsonFailNotifier {

    /** dsp-api 标准事件名（与 CaptureLogParser.TYPE_SUFFIXES 一致） */
    static final String EVENT_JSON_FAIL = "JSON转化失败";

    /** 通知模板 code（对应 system_notify_template.code） */
    static final String TEMPLATE_CODE = "capture_json_fail";

    /** 归属租户（与站内信模板/消息数据一致使用租户 1） */
    private static final Long TENANT_ID = 1L;

    @Resource
    private NotifyMessageSendApi notifyMessageSendApi;

    /** 接收告警的管理员用户ID列表，逗号分隔 */
    @Value("${ssp.capture-notify.admin-user-ids:1}")
    private String adminUserIds;

    /**
     * 维度计数器：key = "预算位ID:流量位ID"。
     * 记录线程（Kafka 消费）与汇总线程（定时）并发读写，使用 ConcurrentHashMap + AtomicLong。
     */
    private final ConcurrentHashMap<String, AtomicLong> counters = new ConcurrentHashMap<>();

    /** 消费到 JSON转化失败 事件时调用（由 CaptureLogConsumer 接线） */
    public void record(Long dspSlotId, Long sspSlotId) {
        String key = dspSlotId + ":" + sspSlotId;
        counters.computeIfAbsent(key, k -> new AtomicLong()).incrementAndGet();
    }

    /**
     * 每分钟汇总推送：swap 出当前窗口的计数，逐维度给每个接收人发站内信。
     * fixedDelay 保证上一轮发完才开始计时，发送慢不会堆积。
     */
    @Scheduled(fixedDelay = 60_000, initialDelay = 60_000)
    public void flushAndNotify() {
        if (counters.isEmpty()) {
            return;
        }
        // swap：新建空表替换，旧表独立遍历，避免发送期间计数丢失
        Map<String, AtomicLong> snapshot;
        synchronized (this) {
            if (counters.isEmpty()) {
                return;
            }
            snapshot = new ConcurrentHashMap<>(counters);
            counters.clear();
        }

        List<Long> userIds = parseUserIds();
        for (Map.Entry<String, AtomicLong> entry : snapshot.entrySet()) {
            long count = entry.getValue().get();
            if (count <= 0) {
                continue;
            }
            String[] dims = entry.getKey().split(":", 2);
            String dspSlotId = dims.length > 0 ? dims[0] : "0";
            String sspSlotId = dims.length > 1 ? dims[1] : "0";
            // 告警时间：本汇总窗口内最后一次失败发生的时刻（不知道几点出的问题就没法排查）
            String time = java.time.LocalDateTime.now()
                    .format(java.time.format.DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
            for (Long userId : userIds) {
                try {
                    NotifySendSingleToUserReqDTO reqDTO = new NotifySendSingleToUserReqDTO();
                    reqDTO.setUserId(userId);
                    reqDTO.setTemplateCode(TEMPLATE_CODE);
                    reqDTO.setTemplateParams(java.util.Map.of(
                            "count", count,
                            "dspSlotId", dspSlotId,
                            "sspSlotId", sspSlotId,
                            "time", time));
                    // 定时线程无租户上下文，站内信落库需指定租户
                    TenantUtils.execute(TENANT_ID, () -> notifyMessageSendApi.sendSingleMessageToAdmin(reqDTO));
                } catch (Exception e) {
                    // 模板未建/被停用等：记日志不中断，其他维度照常
                    log.error("[flushAndNotify][JSON转化失败告警发送失败 userId={} dsp={} ssp={} count={}]",
                            userId, dspSlotId, sspSlotId, count, e);
                }
            }
        }
        log.info("[flushAndNotify][JSON转化失败告警窗口汇总 维度数={} 总次数={}]",
                snapshot.size(), snapshot.values().stream().mapToLong(AtomicLong::get).sum());
    }

    private List<Long> parseUserIds() {
        List<Long> ids = new ArrayList<>();
        for (String s : adminUserIds.split(",")) {
            try {
                long id = Long.parseLong(s.trim());
                if (id > 0) {
                    ids.add(id);
                }
            } catch (NumberFormatException ignore) {
                // 配置脏值跳过
            }
        }
        if (ids.isEmpty()) {
            ids.add(1L); // 兜底超管
        }
        return ids;
    }

}
