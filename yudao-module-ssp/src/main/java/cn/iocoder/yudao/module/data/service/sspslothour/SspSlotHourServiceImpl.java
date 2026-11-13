package cn.iocoder.yudao.module.data.service.sspslothour;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
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
import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.module.data.dal.mysql.sspslothour.SspSlotHourMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SSP_SLOT_HOUR_NOT_EXISTS;
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
public class SspSlotHourServiceImpl implements SspSlotHourService {

    @Resource
    private SspSlotHourMapper sspSlotHourMapper;

    @Resource
    private DspSlotInfoMapper dspSlotInfoMapper;

    @Resource
    private SspSlotInfoMapper sspSlotInfoMapper;

    @Resource
    private LaunchMapper launchMapper;


    @Override
    public Long createSspSlotHour(SspSlotHourSaveReqVO createReqVO) {
        // 插入
        SspSlotHourDO sspSlotHour = BeanUtils.toBean(createReqVO, SspSlotHourDO.class);
        sspSlotHourMapper.insert(sspSlotHour);

        // 返回
        return sspSlotHour.getId();
    }

    @Override
    public void updateSspSlotHour(SspSlotHourSaveReqVO updateReqVO) {
        // 校验存在
        validateSspSlotHourExists(updateReqVO.getId());
        // 更新
        SspSlotHourDO updateObj = BeanUtils.toBean(updateReqVO, SspSlotHourDO.class);
        sspSlotHourMapper.updateById(updateObj);
    }

    @Override
    public void deleteSspSlotHour(Long id) {
        // 校验存在
        validateSspSlotHourExists(id);
        // 删除
        sspSlotHourMapper.deleteById(id);
    }

    @Override
        public void deleteSspSlotHourListByIds(List<Long> ids) {
        // 删除
        sspSlotHourMapper.deleteByIds(ids);
        }


    private void validateSspSlotHourExists(Long id) {
        if (sspSlotHourMapper.selectById(id) == null) {
            throw exception(SSP_SLOT_HOUR_NOT_EXISTS);
        }
    }

    @Override
    public SspSlotHourDO getSspSlotHour(Long id) {
        return sspSlotHourMapper.selectById(id);
    }

    @Override
    public PageResult<SspSlotHourDO> getSspSlotHourPage(SspSlotHourPageReqVO pageReqVO) {
        // 构建动态 ORDER BY，前端已通过 @vben/request 的 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
        String sortField = buildSortField(pageReqVO.getSortingFields());
        Page<SspSlotHourDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<SspSlotHourDO> resultPage = sspSlotHourMapper.selectSspSlotHourPage(page, pageReqVO, sortField);
        resultPage.getRecords().forEach(sspSlotHourDO -> {


            Long reqCount = sspSlotHourDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return;
            }
            // 填充率=返回/请求*100%
            Long retPv = sspSlotHourDO.getRetPv();
            sspSlotHourDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = sspSlotHourDO.getShowPv();
            if (retPv != null && retPv != 0) {
                sspSlotHourDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                sspSlotHourDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = sspSlotHourDO.getClickPv();
            if (showPv != null && showPv != 0) {
                sspSlotHourDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                sspSlotHourDO.setClickRate(0.0);
            }
            if (sspSlotHourDO.getSpend() != null && sspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = sspSlotHourDO.getSpend().add(sspSlotHourDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                sspSlotHourDO.setEcpm(Profit.multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                if (reqCount > 1000000) {
                    sspSlotHourDO.setEcprm(Profit.multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotHourDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());
                } else {
                    sspSlotHourDO.setEcprm(0);
                }

            } else {
                sspSlotHourDO.setEcpm(0.0);
                sspSlotHourDO.setEcprm(0.0);
            }
            if (sspSlotHourDO.getSpend() != null && sspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                sspSlotHourDO.setMediaEcpm(sspSlotHourDO.getSpend().multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000

                    sspSlotHourDO.setMediaEcprm(sspSlotHourDO.getSpend().multiply(BigDecimal.valueOf(1000000))
                            .divide(BigDecimal.valueOf(sspSlotHourDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());

            } else {
                sspSlotHourDO.setMediaEcpm(0.0);
                sspSlotHourDO.setMediaEcprm(0.0);
            }


        });
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }
    private double formatRate(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /* 旧版排序构建：仅支持表真实列（驼峰转下划线），且 hour. 前缀与分页 SQL 的 h 别名不匹配、不支持计算列，已由下方白名单版本替代
     * 构建动态排序字段，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 将 camelCase 转为 snake_case，并校验合法字符防止 SQL 注入
    private String buildSortField(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return "hour.id DESC";
        }
        SortingField sf = sortingFields.get(0);
        String field = sf.getField();
        if (field == null || field.isEmpty()) {
            return "hour.id DESC";
        }
        // camelCase 转 snake_case
        String snake = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        // SQL 注入防护：仅允许字母、数字、下划线（MySQL 中合法列名）
        if (!snake.matches("^[a-zA-Z0-9_]+$")) {
            return "hour.id DESC";
        }
        boolean asc = SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder());
        return "hour." + snake + (asc ? " ASC" : " DESC");
    }
    旧版排序构建结束 */

    /**
     * 可排序字段白名单：排序键 -> ORDER BY 表达式
     * 本查询为聚合查询（GROUP BY ssp_slot_id, date，表别名 h），存储列按 SUM() 排，派生列用表达式排；
     * 派生列表达式与 getSspSlotHourPage 中 Java 计算逻辑口径一致（除零记 0、spend<=0 时 ecpm/ecprm 记 0），
     * 不改变原有计算逻辑，仅用于排序。注意：本表请求 PV 的源列为 req_count（非 req_pv）
     */
    private static final Map<String, String> SORTABLE_FIELDS = new HashMap<>();

    static {
        // 维度列
        SORTABLE_FIELDS.put("date", "h.date");
        SORTABLE_FIELDS.put("sspSlotId", "h.ssp_slot_id");
        SORTABLE_FIELDS.put("sspName", "MAX(ssp.name_alise)");
        SORTABLE_FIELDS.put("appName", "MAX(app.name)");
        SORTABLE_FIELDS.put("mediaName", "MAX(media.media_company_short)");
        // 存储指标列（聚合值）
        SORTABLE_FIELDS.put("reqPv", "SUM(h.req_count)");
        SORTABLE_FIELDS.put("discard", "SUM(h.discard)");
        SORTABLE_FIELDS.put("retDiscard", "SUM(h.ret_discard)");
        SORTABLE_FIELDS.put("retPv", "SUM(h.ret_pv)");
        SORTABLE_FIELDS.put("showPv", "SUM(h.show_pv)");
        SORTABLE_FIELDS.put("clickPv", "SUM(h.click_pv)");
        SORTABLE_FIELDS.put("discountClickPv", "SUM(h.discount_click_pv)");
        SORTABLE_FIELDS.put("discountShowPv", "SUM(h.discount_show_pv)");
        SORTABLE_FIELDS.put("dplsuccPv", "SUM(h.dplsucc_pv)");
        SORTABLE_FIELDS.put("completePv", "SUM(h.complete_pv)");
        SORTABLE_FIELDS.put("installPv", "SUM(h.install_pv)");
        SORTABLE_FIELDS.put("activatePv", "SUM(h.activate_pv)");
        SORTABLE_FIELDS.put("spend", "SUM(h.spend)");
        SORTABLE_FIELDS.put("income", "SUM(h.income)");
        // 派生指标列（SQL 表达式，口径与 Java 计算一致）
        // 填充率 = 返回/请求
        SORTABLE_FIELDS.put("fillRate", "IFNULL(SUM(h.ret_pv) / NULLIF(SUM(h.req_count), 0), 0)");
        // 展现率 = 展示/返回
        SORTABLE_FIELDS.put("displayRate", "IFNULL(SUM(h.show_pv) / NULLIF(SUM(h.ret_pv), 0), 0)");
        // 点击率 = 点击/展示
        SORTABLE_FIELDS.put("clickRate", "IFNULL(SUM(h.click_pv) / NULLIF(SUM(h.show_pv), 0), 0)");
        // 收益 = 成本 + 收入
        SORTABLE_FIELDS.put("revenue", "(SUM(h.spend) + SUM(h.income))");
        // ecpm = (成本+收入)*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("ecpm", "CASE WHEN SUM(h.spend) > 0 THEN IFNULL((SUM(h.spend) + SUM(h.income)) * 1000 / NULLIF(SUM(h.show_pv), 0), 0) ELSE 0 END");
        // 媒体ecpm = 成本*1000/展示，仅当 spend>0
        SORTABLE_FIELDS.put("mediaEcpm", "CASE WHEN SUM(h.spend) > 0 THEN IFNULL(SUM(h.spend) * 1000 / NULLIF(SUM(h.show_pv), 0), 0) ELSE 0 END");
        // ecprm = (成本+收入)*1000000/请求，仅当 spend>0（分母 reqPv，与排序 req_count 口径一致）
        SORTABLE_FIELDS.put("ecprm", "CASE WHEN SUM(h.spend) > 0 THEN IFNULL((SUM(h.spend) + SUM(h.income)) * 1000000 / NULLIF(SUM(h.req_count), 0), 0) ELSE 0 END");
        // 媒体ecprm = 成本*1000000/请求，仅当 spend>0（分母 reqPv，与排序 req_count 口径一致）
        SORTABLE_FIELDS.put("mediaEcprm", "CASE WHEN SUM(h.spend) > 0 THEN IFNULL(SUM(h.spend) * 1000000 / NULLIF(SUM(h.req_count), 0), 0) ELSE 0 END");
    }

    /**
     * 构建动态排序字段（白名单版）：
     * 前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 排序键必须在 SORTABLE_FIELDS 白名单内，命中返回对应 ORDER BY 表达式，未命中回退默认排序；
     * 表达式全部为后端常量，杜绝 SQL 注入
     */
    private String buildSortField(List<SortingField> sortingFields) {
        String defaultSort = "h.date DESC";
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
    public List<SspSlotHourDO> getSspDspSlotHour(Long dspSlotid, Long date) {
        List<SspSlotHourDO> sspDspSlotHour = sspSlotHourMapper.getSspDspSlotHour(dspSlotid, date);
        for (SspSlotHourDO sspSlotHourDO : sspDspSlotHour) {
            Long dspSlotId = sspSlotHourDO.getDspSlotId();
            Long sspSlotId = sspSlotHourDO.getSspSlotId();

            Long reqCount = sspSlotHourDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return null;
            }
            // 填充率=返回/请求*100%
            Long retPv = sspSlotHourDO.getRetPv();
            sspSlotHourDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = sspSlotHourDO.getShowPv();
            if (retPv != null && retPv != 0) {
                sspSlotHourDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                sspSlotHourDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = sspSlotHourDO.getClickPv();
            if (showPv != null && showPv != 0) {
                sspSlotHourDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                sspSlotHourDO.setClickRate(0.0);
            }

            if (sspSlotHourDO.getSpend() != null && sspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = sspSlotHourDO.getSpend().add(sspSlotHourDO.getIncome());

                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                sspSlotHourDO.setEcpm(Profit.multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                sspSlotHourDO.setEcprm(Profit.multiply(BigDecimal.valueOf(1000000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());
            } else {
                sspSlotHourDO.setEcpm(0.0);
                sspSlotHourDO.setEcprm(0.0);
            }
            if (sspSlotHourDO.getSpend() != null && sspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                sspSlotHourDO.setMediaEcpm(sspSlotHourDO.getSpend().multiply(BigDecimal.valueOf(1000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getShowPv()), 3, RoundingMode.HALF_UP).doubleValue());
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                sspSlotHourDO.setMediaEcprm(sspSlotHourDO.getSpend().multiply(BigDecimal.valueOf(1000000))
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getReqPv()), 3, RoundingMode.HALF_UP).doubleValue());
            } else {
                sspSlotHourDO.setMediaEcpm(0.0);
                sspSlotHourDO.setMediaEcprm(0.0);
            }
        }
        // 查看是否解绑
        for (SspSlotHourDO sspSlotHourDO : sspDspSlotHour) {
            List<LaunchDO> launchDOS = launchMapper.selectLaunchBySspSlotIdDspSlotId(sspSlotHourDO.getSspSlotId(),dspSlotid);
            if (launchDOS.size() > 0) {
                sspSlotHourDO.setIsDeleted(1);// 没有解绑
            } else {
                sspSlotHourDO.setIsDeleted(2); // 解绑
            }
        }
        return sspDspSlotHour;
    }

    @Override
    public List<SspSlotHourTrendRespVO> getSspSlotHourTrend(SspSlotHourPageReqVO pageReqVO) {
        // 按小时聚合查询原始指标（SQL 中 MOD(date,100) 已将 date 转为小时值 0~23）
        List<SspSlotHourDO> hourList = sspSlotHourMapper.selectHourTrend(pageReqVO);
        if (CollUtil.isEmpty(hourList)) {
            return Collections.emptyList();
        }

        List<SspSlotHourTrendRespVO> resultList = new ArrayList<>(hourList.size());
        for (SspSlotHourDO hour : hourList) {
            Long reqCount = hour.getReqPv() == null ? 0L : hour.getReqPv();
            Long retPv = hour.getRetPv() == null ? 0L : hour.getRetPv();
            Long showPv = hour.getShowPv() == null ? 0L : hour.getShowPv();
            Long clickPv = hour.getClickPv() == null ? 0L : hour.getClickPv();
            // 小时 DO 中成本、收入为 BigDecimal，趋势 VO 使用 Long，因此安全转换并兼容空值。
            Long spend = hour.getSpend() == null ? 0L : hour.getSpend().longValue();
            Long income = hour.getIncome() == null ? 0L : hour.getIncome().longValue();

            // SQL 返回的 date 字段即为小时值（0~23）
            Integer hourValue = hour.getDate() == null ? 0 : hour.getDate().intValue();

            SspSlotHourTrendRespVO vo = SspSlotHourTrendRespVO.builder()
                    .hour(hourValue)
                    .reqPv(reqCount)
                    .retPv(retPv)
                    .retDiscard(hour.getRetDiscard())
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
                    .ecpm(reqCount != 0 ? income * 1000 / showPv : 0L)
                    // 媒体ecpm = 成本 / 请求数 * 1000
                    .mediaEcpm(reqCount != 0 ? spend * 1000 / showPv : 0L)
                    // ecprm = 收入 / 请求数 * 1000000
                    .ecprm(reqCount != 0 ? income * 1000000 / reqCount : 0L)
                    // 媒体ecprm = 成本 / 请求数 * 1000000
                    .mediaEcprm(reqCount != 0 ? spend * 1000000 / reqCount : 0L)
                    .build();
            resultList.add(vo);
        }
        return resultList;
    }

}
