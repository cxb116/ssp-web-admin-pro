package cn.iocoder.yudao.module.data.service.sspslotday;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.math.RoundingMode;
import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SSP_SLOT_DAY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP-SSP广告位报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SspSlotDayServiceImpl implements SspSlotDayService {

    @Resource
    private SspSlotDayMapper sspSlotDayMapper;

    @Resource
    private DspSlotInfoMapper dspSlotInfoMapper;

    @Resource
    private SspSlotInfoMapper sspSlotInfoMapper;

    @Resource
    private LaunchMapper launchMapper;

    @Override
    public Long createSspSlotDay(SspSlotDaySaveReqVO createReqVO) {
        // 插入
        SspSlotDayDO sspSlotDay = BeanUtils.toBean(createReqVO, SspSlotDayDO.class);
        sspSlotDayMapper.insert(sspSlotDay);

        // 返回
        return sspSlotDay.getId();
    }

    @Override
    public void updateSspSlotDay(SspSlotDaySaveReqVO updateReqVO) {
        // 校验存在
        validateSspSlotDayExists(updateReqVO.getId());
        // 更新
        SspSlotDayDO updateObj = BeanUtils.toBean(updateReqVO, SspSlotDayDO.class);
        sspSlotDayMapper.updateById(updateObj);
    }

    @Override
    public void deleteSspSlotDay(Long id) {
        // 校验存在
        validateSspSlotDayExists(id);
        // 删除
        sspSlotDayMapper.deleteById(id);
    }

    @Override
        public void deleteSspSlotDayListByIds(List<Long> ids) {
        // 删除
        sspSlotDayMapper.deleteByIds(ids);
        }


    private void validateSspSlotDayExists(Long id) {
        if (sspSlotDayMapper.getSspSlotDay(id) == null) {
            throw exception(SSP_SLOT_DAY_NOT_EXISTS);
        }
    }

    @Override
    public SspSlotDayDO getSspSlotDay(Long id) {
        return sspSlotDayMapper.getSspSlotDay(id);
    }


    /***
     *
     * @param pageReqVO 分页查询
     * @return
     */
    @Override
    public PageResult<SspSlotDayDO> getSspSlotDayPage(SspSlotDayPageReqVO pageReqVO) {

        // 构建动态 ORDER BY，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
        String sortField = buildSortField(pageReqVO.getSortingFields());
        Page<SspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<SspSlotDayDO> resultPage = sspSlotDayMapper.selectSspSlotDayPage(page, pageReqVO, sortField);
        // 预算RTB,媒体分成
        resultPage.getRecords().forEach(sspSlotDayDO -> {
           // 需要加一个检索 检索出dspSlotId

            Long reqCount = sspSlotDayDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return;
            }

            // 填充率=返回/请求*100%
            Long retPv = sspSlotDayDO.getRetPv();
            sspSlotDayDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = sspSlotDayDO.getShowPv();
            if (retPv != null && retPv != 0) {
                sspSlotDayDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                sspSlotDayDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = sspSlotDayDO.getClickPv();
            if (showPv != null && showPv != 0) {
                sspSlotDayDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                sspSlotDayDO.setClickRate(0.0);
            }

            if (sspSlotDayDO.getSpend() != null && sspSlotDayDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = sspSlotDayDO.getSpend().add(sspSlotDayDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                sspSlotDayDO.setEcpm(Profit.multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotDayDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000

                    sspSlotDayDO.setEcprm(Profit.multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotDayDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());

            } else {
                sspSlotDayDO.setEcpm(0.0);
                sspSlotDayDO.setEcprm(0.0);
            }
            if (sspSlotDayDO.getSpend() != null && sspSlotDayDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                sspSlotDayDO.setMediaEcpm(sspSlotDayDO.getSpend().multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotDayDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000

                    sspSlotDayDO.setMediaEcprm(sspSlotDayDO.getSpend().multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotDayDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());

            } else {
                sspSlotDayDO.setMediaEcpm(0.0);
                sspSlotDayDO.setMediaEcprm(0.0);
            }

        });
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    private double formatRate(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /* 旧版排序构建：此前未被调用（死代码），仅支持 day 表真实列（驼峰转下划线），不支持填充率等计算列，已由下方白名单版本替代
     * 构建动态排序字段，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 将 camelCase 转为 snake_case，并校验合法字符防止 SQL 注入
    private String buildSortField(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return "day.id DESC";
        }
        SortingField sf = sortingFields.get(0);
        String field = sf.getField();
        if (field == null || field.isEmpty()) {
            return "day.id DESC";
        }
        // camelCase 转 snake_case
        String snake = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        // SQL 注入防护：仅允许字母、数字、下划线（MySQL 中合法列名）
        if (!snake.matches("^[a-zA-Z0-9_]+$")) {
            return "day.id DESC";
        }
        boolean asc = SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder());
        return "day." + snake + (asc ? " ASC" : " DESC");
    }
    旧版排序构建结束 */

    /**
     * 可排序字段白名单：排序键 -> ORDER BY 表达式
     * 本查询为聚合查询（GROUP BY ssp_slot_id, date），存储列按 SUM() 排，派生列用表达式排；
     * 派生列表达式与 getSspSlotDayPage 中 Java 计算逻辑口径一致（除零记 0、spend<=0 时 ecpm/ecprm 记 0），
     * 不改变原有计算逻辑，仅用于排序。注意：本表请求 PV 的源列为 req_count（非 req_pv）
     */
    private static final Map<String, String> SORTABLE_FIELDS = new HashMap<>();

    static {
        // 维度列
        SORTABLE_FIELDS.put("date", "day.date");
        SORTABLE_FIELDS.put("sspSlotId", "day.ssp_slot_id");
        SORTABLE_FIELDS.put("sspName", "MAX(ssp.name_alise)");
        SORTABLE_FIELDS.put("appName", "MAX(app.name)");
        SORTABLE_FIELDS.put("mediaName", "MAX(media.media_company_short)");
        // 存储指标列（聚合值）
        SORTABLE_FIELDS.put("reqPv", "SUM(day.req_count)");
        SORTABLE_FIELDS.put("discard", "SUM(day.discard)");
        SORTABLE_FIELDS.put("retDiscard", "SUM(day.ret_discard)");
        SORTABLE_FIELDS.put("retPv", "SUM(day.ret_pv)");
        SORTABLE_FIELDS.put("showPv", "SUM(day.show_pv)");
        SORTABLE_FIELDS.put("clickPv", "SUM(day.click_pv)");
        SORTABLE_FIELDS.put("discountClickPv", "SUM(day.discount_click_pv)");
        SORTABLE_FIELDS.put("discountShowPv", "SUM(day.discount_show_pv)");
        SORTABLE_FIELDS.put("dplsuccPv", "SUM(day.dplsucc_pv)");
        SORTABLE_FIELDS.put("completePv", "SUM(day.complete_pv)");
        SORTABLE_FIELDS.put("installPv", "SUM(day.install_pv)");
        SORTABLE_FIELDS.put("activatePv", "SUM(day.activate_pv)");
        SORTABLE_FIELDS.put("spend", "SUM(day.spend)");
        SORTABLE_FIELDS.put("income", "SUM(day.income)");
        // 派生指标列（SQL 表达式，口径与 Java 计算一致）
        // 填充率 = 返回/请求
        SORTABLE_FIELDS.put("fillRate", "IFNULL(SUM(day.ret_pv) / NULLIF(SUM(day.req_count), 0), 0)");
        // 展现率 = 展示/返回
        SORTABLE_FIELDS.put("displayRate", "IFNULL(SUM(day.show_pv) / NULLIF(SUM(day.ret_pv), 0), 0)");
        // 点击率 = 点击/展示
        SORTABLE_FIELDS.put("clickRate", "IFNULL(SUM(day.click_pv) / NULLIF(SUM(day.show_pv), 0), 0)");
        // 收益 = 成本 + 收入
        SORTABLE_FIELDS.put("revenue", "(SUM(day.spend) + SUM(day.income))");
        // ecpm = (成本+收入)*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("ecpm", "CASE WHEN SUM(day.spend) > 0 THEN IFNULL((SUM(day.spend) + SUM(day.income)) * 1000 / NULLIF(SUM(day.show_pv), 0), 0) ELSE 0 END");
        // 媒体ecpm = 成本*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("mediaEcpm", "CASE WHEN SUM(day.spend) > 0 THEN IFNULL(SUM(day.spend) * 1000 / NULLIF(SUM(day.show_pv), 0), 0) ELSE 0 END");
        // ecprm = (成本+收入)*1000000/请求，仅当 spend>0
        SORTABLE_FIELDS.put("ecprm", "CASE WHEN SUM(day.spend) > 0 THEN IFNULL((SUM(day.spend) + SUM(day.income)) * 1000000 / NULLIF(SUM(day.req_count), 0), 0) ELSE 0 END");
        // 媒体ecprm = 成本*1000000/请求，仅当 spend>0
        SORTABLE_FIELDS.put("mediaEcprm", "CASE WHEN SUM(day.spend) > 0 THEN IFNULL(SUM(day.spend) * 1000000 / NULLIF(SUM(day.req_count), 0), 0) ELSE 0 END");
    }

    /**
     * 构建动态排序字段（白名单版）：
     * 前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 排序键必须在 SORTABLE_FIELDS 白名单内，命中返回对应 ORDER BY 表达式，未命中回退默认排序；
     * 表达式全部为后端常量，杜绝 SQL 注入
     */
    private String buildSortField(List<SortingField> sortingFields) {
        String defaultSort = "day.date DESC";
        if (sortingFields == null || sortingFields.isEmpty()) {
            return defaultSort;
        }
        SortingField sf = sortingFields.get(0);
        if (sf == null || sf.getField() == null || sf.getField().isEmpty()) {
            return defaultSort;
        }
        String expression = SORTABLE_FIELDS.get(sf.getField());
        if (expression == null) {
            return defaultSort;
        }
        boolean asc = SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder());
        return expression + (asc ? " ASC" : " DESC");
    }

    @Override
    public List<SspSlotDayDO> getDspSspSlotDay(Long dspSlotId,Long date) {
        List<SspSlotDayDO> dspSspSlotDay = sspSlotDayMapper.getDspSspSlotDay(dspSlotId, date);
        for (SspSlotDayDO sspSlotDayDO : dspSspSlotDay) {

            Long reqCount = sspSlotDayDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return null;
            }
            // 填充率=返回/请求*100%
            Long retPv = sspSlotDayDO.getRetPv();
            sspSlotDayDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = sspSlotDayDO.getShowPv();
            if (retPv != null && retPv != 0) {
                sspSlotDayDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                sspSlotDayDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = sspSlotDayDO.getClickPv();
            if (showPv != null && showPv != 0) {
                sspSlotDayDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                sspSlotDayDO.setClickRate(0.0);
            }



            // ecpm（预算千次展示收益）=收益/请求*1000
            // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
            // ecprm（预算百万请求收益）=收益/请求*1000000
            // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000

            if (sspSlotDayDO.getSpend() != null && sspSlotDayDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = sspSlotDayDO.getSpend().add(sspSlotDayDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                sspSlotDayDO.setEcpm(Profit.multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotDayDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    sspSlotDayDO.setEcprm(Profit.multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotDayDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());

            } else {
                sspSlotDayDO.setEcpm(0.0);
                sspSlotDayDO.setEcprm(0.0);
            }
            if (sspSlotDayDO.getSpend() != null && sspSlotDayDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                sspSlotDayDO.setMediaEcpm(sspSlotDayDO.getSpend().multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotDayDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    sspSlotDayDO.setMediaEcprm(sspSlotDayDO.getSpend().multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotDayDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());

            } else {
                sspSlotDayDO.setMediaEcpm(0.0);
                sspSlotDayDO.setMediaEcprm(0.0);
            }

        }


        // 查看是否解绑
        for (SspSlotDayDO sspSlotDayDO : dspSspSlotDay) {
            List<LaunchDO> launchDOS = launchMapper.selectLaunchBySspSlotIdDspSlotId(sspSlotDayDO.getSspSlotId(), dspSlotId);
            if (launchDOS.size() > 0) {
                sspSlotDayDO.setIsDeleted(1);// 没有解绑
            } else {
                sspSlotDayDO.setIsDeleted(2); // 解绑
            }
        }
        return dspSspSlotDay;
    }

    @Override
    public SspSlotDayRespVO getSspSlotDaySum(List<String> date) {



        SspSlotDayDO sspSlotDaySum = sspSlotDayMapper.getDspSlotDaySum(date);
        if (sspSlotDaySum == null) {
            return null;
        }

        BigDecimal spend = Optional.ofNullable(sspSlotDaySum.getSpend()).orElse(BigDecimal.ZERO);
        BigDecimal income = Optional.ofNullable(sspSlotDaySum.getIncome()).orElse(BigDecimal.ZERO);
        long showPv = Optional.ofNullable(sspSlotDaySum.getShowPv()).orElse(0L);
        BigDecimal profit = spend.add(income);

        sspSlotDaySum.setEcpm(calculateMetric(profit, showPv, 1000));
        sspSlotDaySum.setEcprm(calculateMetric(profit, sspSlotDaySum.getReqPv(), 1000000));
        sspSlotDaySum.setMediaEcpm(calculateMetric(spend, showPv, 1000));
        sspSlotDaySum.setMediaEcprm(calculateMetric(spend, sspSlotDaySum.getReqPv(), 1000000));

        return  BeanUtils.toBean(sspSlotDaySum, SspSlotDayRespVO.class);
    }

    @Override
    public SspSlotDayRespVO getSspSlotDaySum(SspSlotDayPageReqVO reqVO) {
        SspSlotDayDO sum = sspSlotDayMapper.getSspSlotDaySumByCondition(reqVO);
        if (sum == null) return null;
        BigDecimal spend = Optional.ofNullable(sum.getSpend()).orElse(BigDecimal.ZERO);
        BigDecimal income = Optional.ofNullable(sum.getIncome()).orElse(BigDecimal.ZERO);
        long show = Optional.ofNullable(sum.getShowPv()).orElse(0L);
        BigDecimal profit = spend.add(income);
        sum.setEcpm(calculateMetric(profit, show, 1000));
        sum.setEcprm(calculateMetric(profit, Optional.ofNullable(sum.getReqPv()).orElse(0L), 1000000));
        sum.setMediaEcpm(calculateMetric(spend, show, 1000));
        sum.setMediaEcprm(calculateMetric(spend, Optional.ofNullable(sum.getReqPv()).orElse(0L), 1000000));
        return BeanUtils.toBean(sum, SspSlotDayRespVO.class);
    }

    // 汇总指标使用 Double 返回，并统一保留 4 位小数。
    private double calculateMetric(BigDecimal amount, long requestCount, int multiplier) {
        if (amount == null || requestCount == 0) {
            return 0.0;
        }
        return amount.multiply(BigDecimal.valueOf(multiplier))
                .divide(BigDecimal.valueOf(requestCount), 3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    @Override
    public List<SspSlotDayDO> getMediaCompanySum(Long date) {
        List<SspSlotDayDO> list = sspSlotDayMapper.selectMediaCompanySum(date);
        for (SspSlotDayDO row : list) {
            long req = nullToZero(row.getReqPv()), ret = nullToZero(row.getRetPv());
            long show = nullToZero(row.getShowPv()), click = nullToZero(row.getClickPv());
            row.setFillRate(req == 0 ? 0D : formatRate(ret * 100.0 / req));
            row.setDisplayRate(ret == 0 ? 0D : formatRate(show * 100.0 / ret));
            row.setClickRate(show == 0 ? 0D : formatRate(click * 100.0 / show));
        }
        return list;
    }

    @Override
    public List<SspSlotDayTrendRespVO> getSspSlotDayTrend(SspSlotDayPageReqVO pageReqVO) {
        // 按天聚合查询原始指标
        List<SspSlotDayDO> dayList = sspSlotDayMapper.selectDayTrend(pageReqVO);
        if (CollUtil.isEmpty(dayList)) {
            return Collections.emptyList();
        }

        List<SspSlotDayTrendRespVO> resultList = new ArrayList<>(dayList.size());
        for (SspSlotDayDO day : dayList) {
            Long reqCount = nullToZero(day.getReqPv());
            Long retPv = nullToZero(day.getRetPv());
            Long showPv = nullToZero(day.getShowPv());
            Long clickPv = nullToZero(day.getClickPv());
            // 成本和收入在 DO 中是 BigDecimal，趋势 VO 仍使用 Long，因此这里仅做空值兼容及类型转换。
            Long spend = day.getSpend() == null ? 0L : day.getSpend().longValue();
            Long income = day.getIncome() == null ? 0L : day.getIncome().longValue();
            BigDecimal spendAmount = day.getSpend() == null ? BigDecimal.ZERO : day.getSpend();
            BigDecimal incomeAmount = day.getIncome() == null ? BigDecimal.ZERO : day.getIncome();

            SspSlotDayTrendRespVO vo = SspSlotDayTrendRespVO.builder()
                    .date(day.getDate())
                    .reqPv(reqCount)
                    .retPv(retPv)
                    .retDiscard(day.getRetDiscard())
                    .showPv(showPv)
                    .clickPv(clickPv)
                    .spend(spend)
                    .income(income)
                    // 填充率 = 返回PV / 请求数 * 100%
                    .fillRate(reqCount != 0 ? formatRate(retPv * 100.0 / reqCount) : 0.0)
                    // 展现率 = 展示PV / 返回PV * 100%
                    .displayRate(retPv != 0 ? formatRate(showPv * 100.0 / retPv) : 0.0)
                    // 点击率 = 点击PV / 展示PV * 100%
                    .clickRate(showPv != 0 ? formatRate(clickPv * 100.0 / showPv) : 0.0)
                    // ecpm = 收入 / 请求数 * 1000
                    .ecpm(reqCount != 0 ? calculateMetric(incomeAmount, showPv, 1000) : 0.0)
                    // 媒体ecpm = 成本 / 请求数 * 1000
                    .mediaEcpm(reqCount != 0 ? calculateMetric(spendAmount, showPv, 1000) : 0.0)
                    // ecprm = 收入 / 请求数 * 1000000
                    .ecprm(reqCount != 0 ? calculateMetric(incomeAmount, reqCount, 1000000) : 0.0)
                    // 媒体ecprm = 成本 / 请求数 * 1000000
                    .mediaEcprm(reqCount != 0 ? calculateMetric(spendAmount, reqCount, 1000000) : 0.0)
                    .build();
            resultList.add(vo);
        }
        return resultList;
    }

    private long nullToZero(Long val) {
        return val == null ? 0L : val;
    }

}
