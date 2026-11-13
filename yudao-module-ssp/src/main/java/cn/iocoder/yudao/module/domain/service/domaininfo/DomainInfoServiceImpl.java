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
    public DomainInfoDO getInfo(Long id) {
        return domainInfoMapper.selectById(id);
    }

    @Override
    public PageResult<DomainInfoRespVO> getInfoPage(DomainInfoPageReqVO pageReqVO) {
        // 0. 今天的日期范围（左闭右开，兼容 data_dsp_slot_day 的 yyyyMMdd / yyyyMMddHH 两种格式）：
        //    请求PV排序子查询和回填 todayReqPv 共用同一范围，保证排序与展示口径一致
        LocalDate today = LocalDate.now();
        long dateBegin = Long.parseLong(today.format(DateTimeFormatter.BASIC_ISO_DATE));
        long dateEnd = Long.parseLong(today.plusDays(1).format(DateTimeFormatter.BASIC_ISO_DATE));
        // 仅支持按请求PV（todayReqPv）排序，白名单转成 ASC/DESC，其余字段走默认排序
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
        List<Long> dspSlotIds = domainInfoMapper.selectDspSlotGroupPage(pageReqVO, offset, limit,
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
                Set<String> recordDomains = new HashSet<>();
                for (String domain : row.getDomains().split("\\|")) {
                    domain = StrUtil.trimToNull(domain);
                    if (domain != null) {
                        recordDomains.add(domain);
                        domainSet.add(domain);
                    }
                }
                maxRecordDomainCount = Math.max(maxRecordDomainCount, recordDomains.size());
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

        // 5. 不再在 Java 里重排：SQL 已按排序条件（默认组内最新 id 倒序）返回本页 dsp_slot_id，顺序即最终顺序
        return new PageResult<>(vos, total);
    }

    @Override
    public List<DomainInfoRespVO> getInfoListByDspSlotId(Long dspSlotId, Integer limit) {
        List<DomainInfoDO> list = domainInfoMapper.selectListByDspSlotId(dspSlotId, limit);
        return BeanUtils.toBean(list, DomainInfoRespVO.class);
    }

    /**
     * 解析排序参数：仅白名单支持按请求PV（todayReqPv）排序，取第一个排序字段；
     * 其他字段或未排序返回 null，走 SQL 默认排序（组内最新 id 倒序）
     */
    private String resolveReqPvSort(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty() || sortingFields.get(0) == null) {
            return null;
        }
        SortingField sf = sortingFields.get(0);
        if (!"todayReqPv".equals(sf.getField())) {
            return null;
        }
        return SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder()) ? "ASC" : "DESC";
    }
}