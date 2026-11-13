package cn.iocoder.yudao.module.capturelog.mq;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.time.LocalDateTime;
import java.util.*;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 捕获日志内存环形缓冲
 *
 * 捕获日志只用于线上问题排查，不落库：Kafka 消费后写入本缓冲，保留最近 N 条
 * （默认 2000，配置 ssp.capture-log.buffer-size），超出自动淘汰最旧的（计数可查）。
 * 重启后清空，历史数据仍在 Kafka（topic log-data，保留 3 天）可重放排查。
 *
 * 内存评估：单条报文典型几 KB~几十 KB（dsp-api 侧 512KB 硬截断），
 * 2000 条上限约几十 MB 量级，可按需调小 buffer-size。
 */
@Slf4j
@Component
public class CaptureLogBuffer {

    /** 列表接口返回的报文预览长度 */
    static final int PREVIEW_LENGTH = 512;

    @Value("${ssp.capture-log.buffer-size:2000}")
    private int maxSize;

    private final AtomicLong seqGenerator = new AtomicLong();
    private final AtomicLong evictedCount = new AtomicLong();

    private Map<Long, CaptureLogVO> buffer;

    @PostConstruct
    public void init() {
        if (maxSize <= 0) {
            maxSize = 2000;
        }
        // accessOrder=false 保持插入顺序；size 超上限自动淘汰最旧一条
        buffer = Collections.synchronizedMap(new LinkedHashMap<Long, CaptureLogVO>(256, 0.75f, false) {
            @Override
            protected boolean removeEldestEntry(Map.Entry<Long, CaptureLogVO> eldest) {
                boolean remove = size() > maxSize;
                if (remove) {
                    evictedCount.incrementAndGet();
                }
                return remove;
            }
        });
        log.info("[init][捕获日志内存缓冲初始化 maxSize={}]", maxSize);
    }

    /**
     * 写入一条（由 Kafka 消费线程调用）
     */
    public void add(CaptureLogVO vo) {
        long seq = seqGenerator.incrementAndGet();
        vo.setSeq(seq);
        if (vo.getCreateTime() == null) {
            vo.setCreateTime(LocalDateTime.now());
        }
        // 报文真实长度统一在此记录（列表接口只带 512 字符预览，前端靠它判定大报文置顶）
        vo.setContentLength(vo.getContent() == null ? 0 : vo.getContent().length());
        buffer.put(seq, vo);
    }

    /**
     * 查询最近日志（倒序，最新在前）
     *
     * @param type      捕获类型 req/res/ssp，null 不过滤
     * @param dspName   DSP名称模糊匹配，null 不过滤
     * @param dspSlotId 预算位ID，null 不过滤
     * @param sspSlotId 流量位ID，null 不过滤
     * @param keyword   报文内容包含匹配，null 不过滤
     * @param limit     返回条数上限（最新在前），默认 100
     */
    public List<CaptureLogVO> list(String type, String dspName, Long dspSlotId, Long sspSlotId,
                                   String keyword, int limit) {
        if (limit <= 0) {
            limit = 100;
        }
        List<CaptureLogVO> snapshot;
        synchronized (buffer) { // synchronizedMap 的迭代/批量操作需在锁内快照
            snapshot = new ArrayList<>(buffer.values());
        }
        // 倒序（最新在前）后过滤
        Collections.reverse(snapshot);
        List<CaptureLogVO> result = new ArrayList<>(Math.min(limit, snapshot.size()));
        for (CaptureLogVO vo : snapshot) {
            if (type != null && !type.isEmpty() && !type.equals(vo.getType())) {
                continue;
            }
            if (dspName != null && !dspName.isEmpty()
                    && (vo.getDspName() == null || !vo.getDspName().contains(dspName))) {
                continue;
            }
            if (dspSlotId != null && !dspSlotId.equals(vo.getDspSlotId())) {
                continue;
            }
            if (sspSlotId != null && !sspSlotId.equals(vo.getSspSlotId())) {
                continue;
            }
            if (keyword != null && !keyword.isEmpty()
                    && (vo.getContent() == null || !vo.getContent().contains(keyword))) {
                continue;
            }
            // 列表返回副本（只带预览），绝不改动缓冲内的原对象 —— 否则 get(seq) 将拿不到全文
            result.add(copyWithPreview(vo));
            if (result.size() >= limit) {
                break;
            }
        }
        return result;
    }

    /**
     * 复制一条仅含预览的 VO（content 不带出，全文走 get(seq)）
     */
    private CaptureLogVO copyWithPreview(CaptureLogVO src) {
        CaptureLogVO copy = new CaptureLogVO();
        copy.setSeq(src.getSeq());
        copy.setType(src.getType());
        copy.setDspName(src.getDspName());
        copy.setDspSlotId(src.getDspSlotId());
        copy.setSspSlotId(src.getSspSlotId());
        copy.setTruncated(src.getTruncated());
        copy.setContentLength(src.getContentLength());
        copy.setCreateTime(src.getCreateTime());
        copy.setPreview(preview(src.getContent()));
        return copy;
    }

    /**
     * 按 seq 获取单条完整报文
     */
    public CaptureLogVO get(Long seq) {
        if (seq == null) {
            return null;
        }
        return buffer.get(seq);
    }

    /**
     * 缓冲状态（排查缓冲是否够用）
     */
    public Map<String, Object> stats() {
        Map<String, Object> stats = new LinkedHashMap<>();
        stats.put("size", buffer.size());
        stats.put("maxSize", maxSize);
        stats.put("evicted", evictedCount.get()); // 被新日志挤出的条数
        return stats;
    }

    private String preview(String content) {
        if (content == null || content.length() <= PREVIEW_LENGTH) {
            return content;
        }
        return content.substring(0, PREVIEW_LENGTH);
    }

}
