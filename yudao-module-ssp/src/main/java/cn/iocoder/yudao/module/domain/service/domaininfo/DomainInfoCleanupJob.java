package cn.iocoder.yudao.module.domain.service.domaininfo;

import cn.iocoder.yudao.module.domain.dal.mysql.domaininfo.DomainInfoMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import java.time.LocalDate;
import java.time.ZoneId;

/** 定期清理昨天以前的 DSP 域名组统计数据。 */
@Slf4j
@Component
public class DomainInfoCleanupJob {

    private static final ZoneId BUSINESS_ZONE = ZoneId.of("Asia/Shanghai");

    @Resource
    private DomainInfoMapper domainInfoMapper;

    /** 每天 00:00 清理昨天以前的数据，保留昨天和今天。 */
    @Scheduled(cron = "0 0 0 * * ?", zone = "Asia/Shanghai")
    public void cleanAtMidnight() {
        cleanOldDomainGroups();
    }

    private void cleanOldDomainGroups() {
        LocalDate cutoffDate = LocalDate.now(BUSINESS_ZONE).minusDays(1);
        int deletedRows = domainInfoMapper.deleteDomainGroupsBeforeDate(cutoffDate);
        log.info("DSP域名组历史数据清理完成，保留日期>={}，删除行数={}", cutoffDate, deletedRows);
    }
}
