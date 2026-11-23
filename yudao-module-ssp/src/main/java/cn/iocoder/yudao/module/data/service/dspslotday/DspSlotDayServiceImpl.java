package cn.iocoder.yudao.module.data.service.dspslotday;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_DAY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;


/**
 * DSP预算广告位日期报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DspSlotDayServiceImpl implements DspSlotDayService {

    @Resource
    private DspSlotDayMapper dspSlotDayMapper;

    @Resource
    private SspSlotDayMapper sspSlotDayMapper;

    @Resource
    private DspSlotInfoMapper dspSlotInfoMapper;

    @Resource
    private SspSlotInfoMapper sspSlotInfoMapper;


    @Resource
    private LaunchMapper launchMapper;





    @Override
    public Long createDspSlotDay(DspSlotDaySaveReqVO createReqVO) {
        // 插入
        DspSlotDayDO dspSlotDay = BeanUtils.toBean(createReqVO, DspSlotDayDO.class);
        dspSlotDayMapper.insert(dspSlotDay);

        // 返回
        return dspSlotDay.getId();
    }

    @Override
    public void updateDspSlotDay(DspSlotDaySaveReqVO updateReqVO) {
        // 校验存在
        validateDspSlotDayExists(updateReqVO.getId());
        // 更新
        DspSlotDayDO updateObj = BeanUtils.toBean(updateReqVO, DspSlotDayDO.class);
        dspSlotDayMapper.updateById(updateObj);
    }

    @Override
    public void deleteDspSlotDay(Long id) {
        // 校验存在
        validateDspSlotDayExists(id);
        // 删除
        dspSlotDayMapper.deleteById(id);
    }

    @Override
        public void deleteDspSlotDayListByIds(List<Long> ids) {
        // 删除
        dspSlotDayMapper.deleteByIds(ids);
        }


    private void validateDspSlotDayExists(Long id) {
        if (dspSlotDayMapper.getDspSlotDay(id) == null) {
            throw exception(DSP_SLOT_DAY_NOT_EXISTS);
        }
    }

    @Override
    public DspSlotDayDO getDspSlotDay(Long id) {
        return dspSlotDayMapper.getDspSlotDay(id);
    }

    @Override
    public PageResult<DspSlotDayDO> getDspSlotDayPage(DspSlotDayPageReqVO pageReqVO) {
        // 构建动态 ORDER BY，前端已通过 @vben/request 的 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
        String sortField = buildSortField(pageReqVO.getSortingFields());
        Page<DspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<DspSlotDayDO> resultPage = dspSlotDayMapper.selectDspSlotDayPage(page, pageReqVO, sortField);

        resultPage.getRecords().forEach(dspSlotDayDO -> {


            Long reqCount = dspSlotDayDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return;
            }

            // 填充率=返回/请求*100%
            Long retPv = dspSlotDayDO.getRetPv();
            dspSlotDayDO.setFillRate(
                    retPv != null && reqCount != 0
                            ? formatRate(retPv * 100.0 / reqCount)
                            : 0.0
            );

            // 展现率=展示/返回*100%
            Long showPv = dspSlotDayDO.getShowPv();
            if (retPv != null && retPv != 0) {
                dspSlotDayDO.setDisplayRate(
                        showPv != null
                                ? formatRate(showPv * 100.0 / retPv)
                                : 0.0
                );
            } else {
                dspSlotDayDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = dspSlotDayDO.getClickPv();
            if (showPv != null && showPv != 0) {
                dspSlotDayDO.setClickRate(
                        clickPv != null
                                ? formatRate(clickPv * 100.0 / showPv)
                                : 0.0
                );
            } else {
                dspSlotDayDO.setClickRate(0.0);
            }



            // ecpm（预算千次展示收益）=收益/展示*1000
            // 媒体ecpm（媒体千次展示收益）=成本/展示*1000
            // ecprm（预算百万请求收益）=收益/展示*1000000
            // 媒体ecprm（媒体百万请求收益）=成本/展示*1000000

            if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {
                BigDecimal Profit = dspSlotDayDO.getSpend().add(dspSlotDayDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 展示 * 1000
                dspSlotDayDO.setEcpm(calculateMetric(Profit, dspSlotDayDO.getShowPv(), 1000));
                // 媒体ecprm（媒体百万请求收益）= 成本 / 展示 * 1000000
                dspSlotDayDO.setEcprm(calculateMetric(Profit, dspSlotDayDO.getReqPv(), 1000000));

            } else {
                dspSlotDayDO.setEcpm(0.0);
                dspSlotDayDO.setEcprm(0.0);
            }
            if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {

                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                dspSlotDayDO.setMediaEcpm(calculateMetric(dspSlotDayDO.getSpend(), dspSlotDayDO.getShowPv(), 1000));
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                dspSlotDayDO.setMediaEcprm(calculateMetric(dspSlotDayDO.getSpend(), dspSlotDayDO.getReqPv(),1000000));
            } else {
                dspSlotDayDO.setMediaEcpm(0.0);
                dspSlotDayDO.setMediaEcprm(0.0);
            }
        });
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public PageResult<DspSlotDayRespExecVo> getDspSlotDayExceVo(DspSlotDayPageReqVO pageReqVO) {
        // 复用分页查询（已 JOIN 出 dspName/companyName/productName/mediaName/sspName/appName/osType）
        Page<DspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<DspSlotDayDO> resultPage = dspSlotDayMapper.selectDspSlotDayPage(page, pageReqVO, "day1.date DESC");
        // 计算派生指标（填充率/展现率/点击率/ecpm 等）—— 与 getDspSlotDayPage 一致
        resultPage.getRecords().forEach(this::enrichDerivedMetrics);
        // 转换为导出 VO
        List<DspSlotDayRespExecVo> list = BeanUtils.toBean(resultPage.getRecords(), DspSlotDayRespExecVo.class);
        return new PageResult<>(list, resultPage.getTotal());
    }

    /**
     * 填充派生指标：填充率、展现率、点击率、ecpm、媒体ecpm、ecprm、媒体ecprm
     * 规则与 getDspSlotDayPage 一致
     */
    private void enrichDerivedMetrics(DspSlotDayDO dspSlotDayDO) {

        Long reqCount = dspSlotDayDO.getReqPv();
        if (reqCount == null || reqCount == 0) {
            return;
        }

        // 填充率=返回/请求*100%
        Long retPv = dspSlotDayDO.getRetPv();
        dspSlotDayDO.setFillRate(
                retPv != null && reqCount != 0
                        ? formatRate(retPv * 100.0 / reqCount)
                        : 0.0
        );

        // 展现率=展示/返回*100%
        Long showPv = dspSlotDayDO.getShowPv();
        if (retPv != null && retPv != 0) {
            dspSlotDayDO.setDisplayRate(
                    showPv != null
                            ? formatRate(showPv * 100.0 / retPv)
                            : 0.0
            );
        } else {
            dspSlotDayDO.setDisplayRate(0.0);
        }

        // 点击率=点击/展示*100%
        Long clickPv = dspSlotDayDO.getClickPv();
        if (showPv != null && showPv != 0) {
            dspSlotDayDO.setClickRate(
                    clickPv != null
                            ? formatRate(clickPv * 100.0 / showPv)
                            : 0.0
            );
        } else {
            dspSlotDayDO.setClickRate(0.0);
        }

        // ecpm（预算千次展示收益）=收益/请求*1000
        // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
        // ecprm（预算百万请求收益）=收益/请求*1000000
        // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000

        if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {
            BigDecimal Profit = dspSlotDayDO.getSpend().add(dspSlotDayDO.getIncome());
            // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
            dspSlotDayDO.setEcpm(calculateMetric(Profit, dspSlotDayDO.getShowPv(), 1000));
            // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
            dspSlotDayDO.setEcprm(calculateMetric(Profit, dspSlotDayDO.getReqPv(), 1000000));
        } else {
            dspSlotDayDO.setEcpm(0.0);
            dspSlotDayDO.setEcprm(0.0);
        }
        if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {
            // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
            dspSlotDayDO.setMediaEcpm(calculateMetric(dspSlotDayDO.getSpend(),  dspSlotDayDO.getShowPv(), 1000));
            // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
            dspSlotDayDO.setMediaEcprm(calculateMetric(dspSlotDayDO.getSpend(),  dspSlotDayDO.getReqPv(), 1000000));
        } else {
            dspSlotDayDO.setMediaEcpm(0.0);
            dspSlotDayDO.setMediaEcprm(0.0);
        }
    }

    private double formatRate(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    private double calculateMetric(BigDecimal amount, long requestCount, int multiplier) {
        if (amount == null || requestCount == 0) {
            return 0.0;
        }
        return amount.multiply(BigDecimal.valueOf(multiplier))
                .divide(BigDecimal.valueOf(requestCount), 3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /* 旧版排序构建：仅支持 day1 表真实列（驼峰转下划线），不支持填充率等计算列，已由下方白名单版本替代
     * 构建动态排序字段，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 将 camelCase 转为 snake_case，并校验合法字符防止 SQL 注入
    private String buildSortField(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return "day1.date DESC";
        }
        SortingField sf = sortingFields.get(0);
        String field = sf.getField();
        if (field == null || field.isEmpty()) {
            return "day1.date DESC";
        }
        // camelCase 转 snake_case
        String snake = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        // SQL 注入防护：仅允许字母、数字、下划线（MySQL 中合法列名）
        if (!snake.matches("^[a-zA-Z0-9_]+$")) {
            return "day1.date DESC";
        }
        boolean asc = SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder());
        return "day1." + snake + (asc ? " ASC" : " DESC");
    }
    旧版排序构建结束 */

    /**
     * 可排序字段白名单：排序键 -> ORDER BY 表达式
     * 本查询为聚合查询（GROUP BY dsp_slot_id, date），存储列按 SUM() 排，派生列用表达式排；
     * 派生列表达式与 getDspSlotDayPage 中 Java 计算逻辑口径一致（除零记 0、spend<=0 时 ecpm/ecprm 记 0），
     * 不改变原有计算逻辑，仅用于排序
     */
    private static final Map<String, String> SORTABLE_FIELDS = new HashMap<>();

    static {
        // 维度列
        SORTABLE_FIELDS.put("date", "day1.date");
        SORTABLE_FIELDS.put("dspSlotId", "day1.dsp_slot_id");
        SORTABLE_FIELDS.put("dspSlotCode", "MAX(day1.dsp_slot_code)");
        SORTABLE_FIELDS.put("dspName", "MAX(dsp.name)");
        SORTABLE_FIELDS.put("companyName", "MAX(com.name)");
        SORTABLE_FIELDS.put("productName", "MAX(pro.name)");
        // 存储指标列（聚合值）
        SORTABLE_FIELDS.put("reqPv", "SUM(day1.req_pv)");
        SORTABLE_FIELDS.put("discard", "SUM(day1.discard)");
        SORTABLE_FIELDS.put("retDiscard", "SUM(day1.ret_discard)");
        SORTABLE_FIELDS.put("retPv", "SUM(day1.ret_pv)");
        SORTABLE_FIELDS.put("showPv", "SUM(day1.show_pv)");
        SORTABLE_FIELDS.put("clickPv", "SUM(day1.click_pv)");
        SORTABLE_FIELDS.put("discountClickPv", "SUM(day1.discount_click_pv)");
        SORTABLE_FIELDS.put("discountShowPv", "SUM(day1.discount_show_pv)");
        SORTABLE_FIELDS.put("dplsuccPv", "SUM(day1.dplsucc_pv)");
        SORTABLE_FIELDS.put("completePv", "SUM(day1.complete_pv)");
        SORTABLE_FIELDS.put("installPv", "SUM(day1.install_pv)");
        SORTABLE_FIELDS.put("activatePv", "SUM(day1.activate_pv)");
        SORTABLE_FIELDS.put("spend", "SUM(day1.spend)");
        SORTABLE_FIELDS.put("income", "SUM(day1.income)");
        // 派生指标列（SQL 表达式，口径与 Java 计算一致）
        // 填充率 = 返回/请求
        SORTABLE_FIELDS.put("fillRate", "IFNULL(SUM(day1.ret_pv) / NULLIF(SUM(day1.req_pv), 0), 0)");
        // 展现率 = 展示/返回
        SORTABLE_FIELDS.put("displayRate", "IFNULL(SUM(day1.show_pv) / NULLIF(SUM(day1.ret_pv), 0), 0)");
        // 点击率 = 点击/展示
        SORTABLE_FIELDS.put("clickRate", "IFNULL(SUM(day1.click_pv) / NULLIF(SUM(day1.show_pv), 0), 0)");
        // 收益 = 成本 + 收入
        SORTABLE_FIELDS.put("revenue", "(SUM(day1.spend) + SUM(day1.income))");
        // ecpm = (成本+收入)*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("ecpm", "CASE WHEN SUM(day1.spend) > 0 THEN IFNULL((SUM(day1.spend) + SUM(day1.income)) * 1000 / NULLIF(SUM(day1.show_pv), 0), 0) ELSE 0 END");
        // 媒体ecpm = 成本*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("mediaEcpm", "CASE WHEN SUM(day1.spend) > 0 THEN IFNULL(SUM(day1.spend) * 1000 / NULLIF(SUM(day1.show_pv), 0), 0) ELSE 0 END");
        // ecprm = (成本+收入)*1000000/请求，仅当 spend>0
        SORTABLE_FIELDS.put("ecprm", "CASE WHEN SUM(day1.spend) > 0 THEN IFNULL((SUM(day1.spend) + SUM(day1.income)) * 1000000 / NULLIF(SUM(day1.req_pv), 0), 0) ELSE 0 END");
        // 媒体ecprm = 成本*1000000/请求，仅当 spend>0
        SORTABLE_FIELDS.put("mediaEcprm", "CASE WHEN SUM(day1.spend) > 0 THEN IFNULL(SUM(day1.spend) * 1000000 / NULLIF(SUM(day1.req_pv), 0), 0) ELSE 0 END");
    }

    /**
     * 构建动态排序字段（白名单版）：
     * 前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 排序键必须在 SORTABLE_FIELDS 白名单内，命中返回对应 ORDER BY 表达式，未命中回退默认排序；
     * 表达式全部为后端常量，杜绝 SQL 注入
     */
    private String buildSortField(List<SortingField> sortingFields) {
        String defaultSort = "day1.date DESC";
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
    public List<DspSlotDayDO> getSSPDspSlotDay(Long sspSlotid, int date) {
        List<DspSlotDayDO> sspDspSlotDay = dspSlotDayMapper.getSSPDspSlotDay(sspSlotid, date);
        for (DspSlotDayDO dspSlotDayDO : sspDspSlotDay) {

            Long reqCount = dspSlotDayDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                // 无请求的行比率记 0，仅跳过本行，不影响整表返回
                dspSlotDayDO.setFillRate(0.0);
                dspSlotDayDO.setDisplayRate(0.0);
                dspSlotDayDO.setClickRate(0.0);
                continue;
            }

            // 填充率=返回/请求*100%
            Long retPv = dspSlotDayDO.getRetPv();
            dspSlotDayDO.setFillRate(
                    retPv != null && reqCount != 0
                            ? formatRate(retPv * 100.0 / reqCount)
                            : 0.0
            );

            // 展现率=展示/返回*100%
            Long showPv = dspSlotDayDO.getShowPv();
            if (retPv != null && retPv != 0) {
                dspSlotDayDO.setDisplayRate(
                        showPv != null
                                ? formatRate(showPv * 100.0 / retPv)
                                : 0.0
                );
            } else {
                dspSlotDayDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = dspSlotDayDO.getClickPv();
            if (showPv != null && showPv != 0) {
                dspSlotDayDO.setClickRate(
                        clickPv != null
                                ? formatRate(clickPv * 100.0 / showPv)
                                : 0.0
                );
            } else {
                dspSlotDayDO.setClickRate(0.0);
            }

            // ecpm（预算千次展示收益）=收益/请求*1000
            // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
            // ecprm（预算百万请求收益）=收益/请求*1000000
            // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000
            if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {
                BigDecimal Profit = dspSlotDayDO.getSpend().add(dspSlotDayDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                dspSlotDayDO.setEcpm(calculateMetric(Profit, dspSlotDayDO.getShowPv(), 1000));
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                dspSlotDayDO.setEcprm(calculateMetric(Profit, dspSlotDayDO.getReqPv(), 1000000));
            } else {
                dspSlotDayDO.setEcpm(0.0);
                dspSlotDayDO.setEcprm(0.0);
            }
            if (dspSlotDayDO.getSpend() != null && dspSlotDayDO.getSpend().signum() > 0) {
                // ecpm（预算千次展示收益）= 收益 / 曝光 * 1000
                dspSlotDayDO.setMediaEcpm(calculateMetric(dspSlotDayDO.getSpend(), dspSlotDayDO.getShowPv(), 1000));
                // ecprm（预算百万请求收益）= 收益 / 曝光 * 1000000
                dspSlotDayDO.setMediaEcprm(calculateMetric(dspSlotDayDO.getSpend(), dspSlotDayDO.getReqPv(), 1000000));

            } else {
                dspSlotDayDO.setMediaEcpm(0.0);
                dspSlotDayDO.setMediaEcprm(0.0);
            }

        }
        // 查看是否解绑：一次查出该媒体广告位的全部绑定关系，避免逐行查询（N+1）；
        // 两条 SQL 均按 dsp_launch 原表匹配（不过滤 deleted），与原逐行查询结果一致
        Set<Long> boundDspSlotIds = new HashSet<>();
        for (LaunchDO launchDO : launchMapper.selectLaunchBySspSlotId(sspSlotid)) {
            boundDspSlotIds.add(launchDO.getDspSlotId());
        }
        for (DspSlotDayDO dspSlotDayDO : sspDspSlotDay) {
            // 1=没有解绑 2=解绑
            dspSlotDayDO.setIsDeleted(boundDspSlotIds.contains(dspSlotDayDO.getDspSlotId()) ? 1 : 2);
        }
        return sspDspSlotDay;
    }

    @Override
    public DspSlotDayRespVO getDspSlotDaySum(List<String> date) {
        DspSlotDayDO dspSlotDaySum = dspSlotDayMapper.getDspSlotDaySum(date);
        if (dspSlotDaySum == null) {
            return null;
        }

        BigDecimal spend = Optional.ofNullable(dspSlotDaySum.getSpend()).orElse(BigDecimal.ZERO);
        BigDecimal income = Optional.ofNullable(dspSlotDaySum.getIncome()).orElse(BigDecimal.ZERO);
        long showPv = Optional.ofNullable(dspSlotDaySum.getShowPv()).orElse(0L);
        BigDecimal profit = spend.add(income);
        dspSlotDaySum.setEcpm(calculateMetric(profit, showPv, 1000));
        dspSlotDaySum.setEcprm(calculateMetric(profit, dspSlotDaySum.getReqPv(), 1000000));
        dspSlotDaySum.setMediaEcpm(calculateMetric(spend, showPv, 1000));
        dspSlotDaySum.setMediaEcprm(calculateMetric(spend, dspSlotDaySum.getReqPv(), 1000000));
        return BeanUtils.toBean(dspSlotDaySum, DspSlotDayRespVO.class);
    }

    @Override
    public DspSlotDayRespVO getDspSlotDaySum(DspSlotDayPageReqVO reqVO) {
        DspSlotDayDO sum = dspSlotDayMapper.getDspSlotDaySumByCondition(reqVO);
        return sum == null ? null : BeanUtils.toBean(sum, DspSlotDayRespVO.class);
    }

    @Override
    public List<DspSlotDayDO> getDspCompanySum(Long date) {
        List<DspSlotDayDO> list = dspSlotDayMapper.selectDspCompanySum(date);
        for (DspSlotDayDO row : list) {
            long reqPv = nullToZero(row.getReqPv());
            long retPv = nullToZero(row.getRetPv());
            long showPv = nullToZero(row.getShowPv());
            long clickPv = nullToZero(row.getClickPv());
            row.setFillRate(reqPv == 0 ? 0D : formatRate(retPv * 100.0 / reqPv));
            row.setDisplayRate(retPv == 0 ? 0D : formatRate(showPv * 100.0 / retPv));
            row.setClickRate(showPv == 0 ? 0D : formatRate(clickPv * 100.0 / showPv));
        }
        return list;
    }

    @Override
    public PageResult<DspSlotDayDO> getDspSlotDayPageDetail(DspSlotDayPageReqVO pageReqVO) {
        PageResult<DspSlotDayDO> dspSlotDayPageDetail = dspSlotDayMapper.getDspSlotDayPageDetail(pageReqVO);
        return dspSlotDayPageDetail;
    }

    @Override
    public PageResult<DspSlotDayDO> getDspSlotDayPageInfo(DspSlotDayPageReqVO pageReqVO) {
        return getDspSlotDayPageDetail(pageReqVO);
    }

//    @Override
//    public PageResult<DspSlotDayRespExecVo> getDspSlotDayDeatil(SspSlotDayPageReqVO pageReqVO) {
//        Page<DspSlotDayRespExecVo> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
//        Page<DspSlotDayRespExecVo> resultPage = dspSlotDayMapper.getDspSlotDayDeatil(page, pageReqVO);
//
//        resultPage.getRecords().forEach(dspSlotDayDO -> {
//            Long dspSlotId = dspSlotDayDO.getDspSlotId();
//            Long sspSlotId = dspSlotDayDO.getSspSlotId();
//            // 查找预算DspSlotInfo 对象
//            DspSlotInfoDO dspSlotInfoDO = dspSlotInfoMapper.selectById(dspSlotId);
//            if (dspSlotInfoDO == null) {
//                return;
//            }
//            // 上游预算结算方式，1=分成，2=RTB 3=固价
//            Integer dspPayType = dspSlotInfoDO.getDspPayType();
//            if (dspPayType == null) {
//                return;
//            }
//
//            SspSlotInfoDO sspSlotInfoDO = sspSlotInfoMapper.selectById(sspSlotId);
//            if (sspSlotInfoDO == null) {
//                return;
//            }
//            // 下游媒体结算方式，1=分成，2=RTB, 3=固价
//            Integer sspPayType = sspSlotInfoDO.getSspPayType();
//            if (sspPayType == null) {
//                return;
//            }
//            Long reqCount = dspSlotDayDO.getReqPv();
//            if (reqCount == null || reqCount == 0) {
//                return;
//            }
//
//            // 填充率=返回/请求*100%
//            Long retPv = dspSlotDayDO.getRetPv();
//            dspSlotDayDO.setFillRate(
//                    retPv != null && reqCount != 0
//                            ? formatRate(retPv * 100.0 / reqCount)
//                            : 0.0
//            );
//
//            // 展现率=展示/返回*100%
//            Long showPv = dspSlotDayDO.getShowPv();
//            if (retPv != null && retPv != 0) {
//                dspSlotDayDO.setDisplayRate(
//                        showPv != null
//                                ? formatRate(showPv * 100.0 / retPv)
//                                : 0.0
//                );
//            } else {
//                dspSlotDayDO.setDisplayRate(0.0);
//            }
//
//            // 点击率=点击/展示*100%
//            Long clickPv = dspSlotDayDO.getClickPv();
//            if (showPv != null && showPv != 0) {
//                dspSlotDayDO.setClickRate(
//                        clickPv != null
//                                ? formatRate(clickPv * 100.0 / showPv)
//                                : 0.0
//                );
//            } else {
//                dspSlotDayDO.setClickRate(0.0);
//            }
//
//            // ecpm（预算千次展示收益）=收益/请求*1000
//            // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
//            // ecprm（预算百万请求收益）=收益/请求*1000000
//            // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000
//
//            // 预算RTB,媒体RTB
//            if (dspPayType == 2 && sspPayType == 2) {
//                Long income = dspSlotDayDO.getIncome();
//                if (income != null && income != 0) {
//                    dspSlotDayDO.setEcpm((double) (income * 1000 / reqCount));
//                    dspSlotDayDO.setEcprm((double) (income * 1000000 / reqCount));
//                } else {
//                    dspSlotDayDO.setEcpm((double) 0L);
//                    dspSlotDayDO.setEcprm((double) 0L);
//                }
//
//                Long spend = dspSlotDayDO.getSpend();
//                if (spend != null && spend != 0) {
//                    dspSlotDayDO.setMediaEcpm((double) (spend * 1000 / reqCount));
//                    dspSlotDayDO.setMediaEcprm((double) (spend * 1000000 / reqCount));
//                } else {
//                    dspSlotDayDO.setMediaEcpm((double) 0L);
//                    dspSlotDayDO.setMediaEcprm((double) 0L);
//                }
//            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
//                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
//                if (sspDealRatio == null) {
//                    sspDealRatio = 0;
//                }
//                Long spend = dspSlotDayDO.getSpend();
//                if (spend != null && spend != 0 && sspDealRatio != 0) {
//                    // 预算收入 = 媒体成本 * 分成系数 / 100
//                    Long income = spend * sspDealRatio / 100;
//                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
//                    dspSlotDayDO.setEcpm((double) (income * 1000 / reqCount));
//                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
//                    dspSlotDayDO.setEcprm((double) (income * 1000000 / reqCount));
//
//                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
//                    dspSlotDayDO.setMediaEcpm((double) (spend * 1000 / reqCount));
//                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
//                    dspSlotDayDO.setMediaEcprm((double) (spend * 1000000 / reqCount));
//                } else {
//                    dspSlotDayDO.setEcpm((double) 0L);
//                    dspSlotDayDO.setEcprm((double) 0L);
//                    dspSlotDayDO.setMediaEcpm((double) 0L);
//                    dspSlotDayDO.setMediaEcprm((double) 0L);
//                }
//            }
//        });
//        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
//    }

    private long nullToZero(Long val) {
        return val == null ? 0L : val;
    }

    private long decimalToLong(BigDecimal val) {
        return val == null ? 0L : val.longValue();
    }

    @Override
    public List<DspSlotDayTrendRespVO> getDspSlotDayTrend(DspSlotDayPageReqVO pageReqVO) {
        // 按天聚合查询原始指标
        List<DspSlotDayDO> dayList = dspSlotDayMapper.selectDayTrend(pageReqVO);
        if (CollUtil.isEmpty(dayList)) {
            return Collections.emptyList();
        }

        List<DspSlotDayTrendRespVO> resultList = new ArrayList<>(dayList.size());
        for (DspSlotDayDO day : dayList) {
            Long reqCount = nullToZero(day.getReqPv());
            Long retPv = nullToZero(day.getRetPv());
            Long showPv = nullToZero(day.getShowPv());
            Long clickPv = nullToZero(day.getClickPv());
            Long spend = decimalToLong(day.getSpend());
            Long income = decimalToLong(day.getIncome());
            Long po = spend + income;
            DspSlotDayTrendRespVO vo = DspSlotDayTrendRespVO.builder()
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
                    // ecpm（千次展示收益）= 收入 / 展示数 * 1000
                    .ecpm(showPv != 0 ? po * 1000 / showPv : 0L)
                    // 媒体ecpm（媒体千次展示收益）= 成本 / 展示数 * 1000
                    .mediaEcpm(showPv != 0 ? spend * 1000 / showPv : 0L)
                    // ecprm = 收入 / 请求数 * 1000000
                    .ecprm(reqCount != 0 ? po * 1000000 / reqCount : 0L)
                    // 媒体ecprm = 成本 / 请求数 * 1000000
                    .mediaEcprm(reqCount != 0 ? spend * 1000000 / reqCount : 0L)
                    .build();
            resultList.add(vo);
        }
        return resultList;
    }


}
