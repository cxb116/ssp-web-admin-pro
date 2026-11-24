package cn.iocoder.yudao.module.domain.service.domaininfo;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaininfo.DomainInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;

import cn.iocoder.yudao.module.domain.dal.mysql.domaininfo.DomainInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.company.CompanyMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INFO_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP域名详情 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DomainInfoServiceImpl implements DomainInfoService {

    private static final String SORT_FIELD_DOMAIN_COUNT = "domainCount";
    private static final String SORT_FIELD_MAX_DOMAIN_LENGTH = "maxDomainLength";

    @Resource
    private DomainInfoMapper domainInfoMapper;

    @Resource
    private DspSlotDayMapper dspSlotDayMapper;

    /** 【性能】名称在后端带出：两次 IN 主键查询替代前端全量拉预算位表（曾阻塞列表查询致页面卡死） */
    @Resource
    private DspSlotInfoMapper dspSlotInfoMapper;
    @Resource
    private CompanyMapper dspCompanyMapper;

    @Override
    public Long createInfo(DomainInfoSaveReqVO createReqVO) {
        // 插入
        DomainInfoDO info = BeanUtils.toBean(createReqVO, DomainInfoDO.class);
        domainInfoMapper.insert(info);

        // 返回
        return info.getId();
    }

    @Override
    public void updateInfo(DomainInfoSaveReqVO updateReqVO) {
        // 校验存在
        validateInfoExists(updateReqVO.getId());
        // 更新
        DomainInfoDO updateObj = BeanUtils.toBean(updateReqVO, DomainInfoDO.class);
        domainInfoMapper.updateById(updateObj);
    }

    @Override
    public void deleteInfo(Long id) {
        // 校验存在
        validateInfoExists(id);
        // 删除
        domainInfoMapper.deleteById(id);
    }

    @Override
        public void deleteInfoListByIds(List<Long> ids) {
        // 删除
        domainInfoMapper.deleteByIds(ids);
        }


    private void validateInfoExists(Long id) {
        if (domainInfoMapper.selectById(id) == null) {
            throw exception(INFO_NOT_EXISTS);
        }
    }

    @Override
    public DomainInfoRespVO getInfo(Long id) {
        DomainInfoDO row = domainInfoMapper.selectById(id);
        if (row == null) {
            return null;
        }
        DomainInfoRespVO vo = BeanUtils.toBean(row, DomainInfoRespVO.class);
        LocalDate today = LocalDate.now();
        Long total = dailyCountTotal(row.getDspSlotId(), today);
        vo.setProportion(calculateDailyProportion(row.getProportion(), row.getProportionDate(), today, total));
        return vo;
    }

    @Override
    public PageResult<DomainInfoRespVO> getInfoPage(DomainInfoPageReqVO pageReqVO) {
        // 0. 今天的日期范围（左闭右开，兼容 data_dsp_slot_day 的 yyyyMMdd / yyyyMMddHH 两种格式）：
        //    请求PV排序子查询和回填 todayReqPv 共用同一范围，保证排序与展示口径一致
        LocalDate today = LocalDate.now();
        long dateBegin = Long.parseLong(today.format(DateTimeFormatter.BASIC_ISO_DATE));
        long dateEnd = Long.parseLong(today.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE));
        // 域名统计排序（domainCount/maxDomainLength）需要按全量分组计算后排序；
        // 请求PV排序仍下推到 SQL，其余字段走默认排序
        SortingField domainSort = resolveDomainSort(pageReqVO.getSortingFields());
        String orderByReqPv = resolveReqPvSort(pageReqVO.getSortingFields());

        // 1. 按 dsp_slot_id 分组做真正的分页：先查组数，再取本页的 dsp_slot_id（SQL 内已按请求PV排序）
        Long total = domainInfoMapper.selectDspSlotGroupCount(pageReqVO);
        if (total == null || total == 0) {
            return PageResult.empty(total);
        }
        // PAGE_SIZE_NONE（-1）表示不分页，Excel 导出在用
        Integer limit = pageReqVO.getPageSize() != null && pageReqVO.getPageSize() < 0
                ? null : pageReqVO.getPageSize();
        int offset = limit == null ? 0 : (pageReqVO.getPageNo() - 1) * limit;
        List<Long> dspSlotIds = domainSort != null
                ? selectDspSlotIdsSortedByDomain(pageReqVO, offset, limit, domainSort)
                : domainInfoMapper.selectDspSlotGroupPage(pageReqVO, offset, limit,
                        dateBegin, dateEnd, orderByReqPv);
        if (dspSlotIds.isEmpty()) {
            return PageResult.empty(total);
        }

        // 2. 查出本页这些 dsp_slot_id 的全部记录，组内把 domains 按 | 拆开去重、重组成一条
        List<DomainInfoDO> rows = domainInfoMapper.selectList(
                new LambdaQueryWrapperX<DomainInfoDO>().in(DomainInfoDO::getDspSlotId, dspSlotIds));
        Map<Long, List<DomainInfoDO>> groups = new HashMap<>();
        for (DomainInfoDO row : rows) {
            groups.computeIfAbsent(row.getDspSlotId(), k -> new ArrayList<>()).add(row);
        }

        // 3.0 名称回填（一次 IN 查询带出，前端不再全量拉预算位表）：
        //     预算位名称 + 经 companyId 关联的预算公司名称
        Map<Long, DspSlotInfoDO> slotMap = new HashMap<>();
        Map<Long, String> companyNameMap = new HashMap<>();
        try {
            for (DspSlotInfoDO slot : dspSlotInfoMapper.selectByIds(dspSlotIds)) {
                if (slot != null && slot.getId() != null) {
                    slotMap.put(slot.getId(), slot);
                }
            }
            Set<Long> companyIds = new HashSet<>();
            for (DspSlotInfoDO slot : slotMap.values()) {
                if (slot.getCompanyId() != null) {
                    companyIds.add(slot.getCompanyId());
                }
            }
            if (!companyIds.isEmpty()) {
                for (CompanyDO company : dspCompanyMapper.selectByIds(companyIds)) {
                    if (company != null && company.getId() != null) {
                        companyNameMap.put(company.getId(), company.getName());
                    }
                }
            }
        } catch (Exception ignore) {
            // 名称回填失败不影响列表主体，前端回退显示 ID
        }

        // 3. 组装本页的行：取组内 id 最大（最新）的一条作为代表行，domains 用去重后的并集；
        //    maxDomainLength = 组内各条记录中，单条 domains 域名数量最多的那个数量
        List<DomainInfoRespVO> vos = new ArrayList<>(dspSlotIds.size());
        for (Long dspSlotId : dspSlotIds) {
            List<DomainInfoDO> group = groups.get(dspSlotId);
            if (group == null) {
                continue;
            }
            DomainInfoDO latest = null;
            TreeSet<String> domainSet = new TreeSet<>();
            int maxRecordDomainCount = 0;
            for (DomainInfoDO row : group) {
                if (latest == null || (row.getId() != null && row.getId() > latest.getId())) {
                    latest = row;
                }
                if (StrUtil.isBlank(row.getDomains())) {
                    continue;
                }
                Set<String> recordDomains = parseDomains(row.getDomains());
                if (!recordDomains.isEmpty()) {
                    domainSet.addAll(recordDomains);
                    maxRecordDomainCount = Math.max(maxRecordDomainCount, recordDomains.size());
                }
            }
            DomainInfoRespVO vo = new DomainInfoRespVO();
            vo.setId(latest.getId());
            vo.setDspSlotId(latest.getDspSlotId());
            DspSlotInfoDO slot = slotMap.get(dspSlotId);
            vo.setDspSlotName(slot != null ? slot.getName() : null);
            vo.setDspCompanyName(slot != null ? companyNameMap.get(slot.getCompanyId()) : null);
            vo.setSspSlotId(latest.getSspSlotId());
            vo.setDomains(String.join("|", domainSet));
            vo.setCreateTime(latest.getCreateTime());
            vo.setMaxDomainLength(maxRecordDomainCount);
            vos.add(vo);
        }

        // 4. 带出今天的请求PV：data_dsp_slot_day 按 dsp_slot_id + 当天汇总 req_pv（日期范围与排序子查询一致）
        Map<Long, Long> reqPvMap = new HashMap<>();
        for (DspSlotDayDO day : dspSlotDayMapper.selectReqPvSumByDspSlotIds(dspSlotIds, dateBegin, dateEnd)) {
            if (day.getDspSlotId() != null) {
                reqPvMap.merge(day.getDspSlotId(), day.getReqPv() == null ? 0L : day.getReqPv(), Long::sum);
            }
        }
        for (DomainInfoRespVO vo : vos) {
            vo.setTodayReqPv(reqPvMap.getOrDefault(vo.getDspSlotId(), 0L));
        }

        // 5. 顺序由前面返回的 dspSlotIds 决定：请求PV由 SQL 排序，域名统计由 Java 全量排序
        return new PageResult<>(vos, total);
    }

    @Override
    public List<DomainInfoRespVO> getInfoListByDspSlotId(Long dspSlotId, Integer limit) {
        List<DomainInfoDO> list = domainInfoMapper.selectListByDspSlotId(dspSlotId, limit);
        List<DomainInfoRespVO> vos = BeanUtils.toBean(list, DomainInfoRespVO.class);
        LocalDate today = LocalDate.now();
        Long totalCount = dailyCountTotal(dspSlotId, today);
        for (int i = 0; i < list.size(); i++) {
            DomainInfoDO row = list.get(i);
            vos.get(i).setProportion(calculateDailyProportion(row.getProportion(), row.getProportionDate(),
                    today, totalCount));
        }
        return vos;
    }

    private Long dailyCountTotal(Long dspSlotId, LocalDate statDate) {
        return domainInfoMapper.selectDailyGroupCountTotal(dspSlotId, statDate);
    }

    /** API 仍用百万分比传百分比，前端除以 10,000 后显示百分数。 */
    private Long calculateDailyProportion(Long groupCount, LocalDate proportionDate,
                                          LocalDate today, Long totalCount) {
        if (groupCount == null || groupCount <= 0 || !today.equals(proportionDate)
                || totalCount == null || totalCount <= 0) {
            return null;
        }
        return Math.round((double) groupCount * 1_000_000D / totalCount);
    }

    /**
     * 按域名总数、单组域名最大数对全部分组排序，再截取当前页。
     * 统计口径与列表展示一致：先按过滤条件确定分组，再合并组内全部记录的域名。
     */
    private List<Long> selectDspSlotIdsSortedByDomain(DomainInfoPageReqVO reqVO, int offset, Integer limit,
                                                       SortingField sortingField) {
        List<DomainInfoDO> rows = domainInfoMapper.selectDspSlotGroupDomainRows(reqVO);
        Map<Long, DomainGroupStat> statMap = new HashMap<>();
        for (DomainInfoDO row : rows) {
            if (row.getDspSlotId() == null) {
                continue;
            }
            DomainGroupStat stat = statMap.computeIfAbsent(row.getDspSlotId(), DomainGroupStat::new);
            if (row.getId() != null && row.getId() > stat.maxId) {
                stat.maxId = row.getId();
            }
            Set<String> recordDomains = parseDomains(row.getDomains());
            if (recordDomains.isEmpty()) {
                continue;
            }
            stat.domains.addAll(recordDomains);
            stat.maxDomainLength = Math.max(stat.maxDomainLength, recordDomains.size());
        }

        List<DomainGroupStat> stats = new ArrayList<>(statMap.values());
        Comparator<DomainGroupStat> comparator;
        if (SORT_FIELD_DOMAIN_COUNT.equals(sortingField.getField())) {
            comparator = Comparator.comparingInt((DomainGroupStat stat) -> stat.domains.size());
        } else {
            comparator = Comparator.comparingInt((DomainGroupStat stat) -> stat.maxDomainLength);
        }
        if (!SortingField.ORDER_ASC.equalsIgnoreCase(sortingField.getOrder())) {
            comparator = comparator.reversed();
        }
        // 统计值相同时保持默认的组内最新 id 倒序，避免分页时顺序不稳定
        comparator = comparator.thenComparing(
                Comparator.comparingLong((DomainGroupStat stat) -> stat.maxId).reversed());
        stats.sort(comparator);

        int from = limit == null ? 0 : Math.min(offset, stats.size());
        int to = limit == null ? stats.size() : Math.min(from + limit, stats.size());
        List<Long> dspSlotIds = new ArrayList<>(to - from);
        for (int i = from; i < to; i++) {
            dspSlotIds.add(stats.get(i).dspSlotId);
        }
        return dspSlotIds;
    }

    /** 按 | 拆分域名，并做 trim、去空、组内去重，和列表展示口径保持一致 */
    private Set<String> parseDomains(String domains) {
        Set<String> result = new HashSet<>();
        if (StrUtil.isBlank(domains)) {
            return result;
        }
        for (String domain : domains.split("\\|")) {
            domain = StrUtil.trimToNull(domain);
            if (domain != null) {
                result.add(domain);
            }
        }
        return result;
    }

    /** 解析域名统计排序字段；其他字段返回 null */
    private SortingField resolveDomainSort(List<SortingField> sortingFields) {
        SortingField sortField = getFirstSortingField(sortingFields);
        if (sortField == null) {
            return null;
        }
        return SORT_FIELD_DOMAIN_COUNT.equals(sortField.getField())
                || SORT_FIELD_MAX_DOMAIN_LENGTH.equals(sortField.getField()) ? sortField : null;
    }

    /**
     * 解析排序参数：仅白名单支持按请求PV（todayReqPv）排序，取第一个排序字段；
     * 其他字段或未排序返回 null，走 SQL 默认排序（组内最新 id 倒序）
     */
    private String resolveReqPvSort(List<SortingField> sortingFields) {
        SortingField sf = getFirstSortingField(sortingFields);
        if (sf == null || !"todayReqPv".equals(sf.getField())) {
            return null;
        }
        return SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder()) ? "ASC" : "DESC";
    }

    private SortingField getFirstSortingField(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty() || sortingFields.get(0) == null) {
            return null;
        }
        return sortingFields.get(0);
    }

    private static class DomainGroupStat {

        private final Long dspSlotId;
        private final Set<String> domains = new HashSet<>();
        private int maxDomainLength;
        private long maxId;

        private DomainGroupStat(Long dspSlotId) {
            this.dspSlotId = dspSlotId;
        }
    }
}
