package cn.iocoder.yudao.module.data.service.dspslothour;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.dspslothour.DspSlotHourMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_HOUR_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP预算广告位小时报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DspSlotHourServiceImpl implements DspSlotHourService {

    @Resource
    private DspSlotHourMapper dspSlotHourMapper;

    @Resource
    private SspSlotDayMapper sspSlotDayMapper;

    @Resource
    private DspSlotInfoMapper dspSlotInfoMapper;

    @Resource
    private SspSlotInfoMapper sspSlotInfoMapper;

    @Resource
    private LaunchMapper launchMapper;

    @Override
    public Long createDspSlotHour(DspSlotHourSaveReqVO createReqVO) {
        // 插入
        DspSlotHourDO dspSlotHour = BeanUtils.toBean(createReqVO, DspSlotHourDO.class);
        dspSlotHourMapper.insert(dspSlotHour);

        // 返回
        return dspSlotHour.getId();
    }

    @Override
    public void updateDspSlotHour(DspSlotHourSaveReqVO updateReqVO) {
        // 校验存在
        validateDspSlotHourExists(updateReqVO.getId());
        // 更新
        DspSlotHourDO updateObj = BeanUtils.toBean(updateReqVO, DspSlotHourDO.class);
        dspSlotHourMapper.updateById(updateObj);
    }

    @Override
    public void deleteDspSlotHour(Long id) {
        // 校验存在
        validateDspSlotHourExists(id);
        // 删除
        dspSlotHourMapper.deleteById(id);
    }

    @Override
        public void deleteDspSlotHourListByIds(List<Long> ids) {
        // 删除
        dspSlotHourMapper.deleteByIds(ids);
        }


    private void validateDspSlotHourExists(Long id) {
        if (dspSlotHourMapper.selectById(id) == null) {
            throw exception(DSP_SLOT_HOUR_NOT_EXISTS);
        }
    }

    @Override
    public DspSlotHourDO getDspSlotHour(Long id) {
        return dspSlotHourMapper.selectById(id);
    }

    @Override
    public PageResult<DspSlotHourDO> getDspSlotHourPage(DspSlotHourPageReqVO pageReqVO) {
        // 构建动态 ORDER BY
        String sortField = buildSortField(pageReqVO.getSortingFields());
        Page<DspSlotHourDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<DspSlotHourDO> resultPage = dspSlotHourMapper.selectDspSlotHourPage(page, pageReqVO, sortField);

        resultPage.getRecords().forEach(dspSlotHourDO -> {

            Long reqCount = dspSlotHourDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return;
            }
            // 填充率=返回/请求*100%
            Long retPv = dspSlotHourDO.getRetPv();
            dspSlotHourDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = dspSlotHourDO.getShowPv();
            if (retPv != null && retPv != 0) {
                dspSlotHourDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                dspSlotHourDO.setDisplayRate(0.0);
            }

            // 点击率=点击/展示*100%
            Long clickPv = dspSlotHourDO.getClickPv();
            if (showPv != null && showPv != 0) {
                dspSlotHourDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                dspSlotHourDO.setClickRate(0.0);
            }

            if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = dspSlotHourDO.getSpend().add(dspSlotHourDO.getIncome());


                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                dspSlotHourDO.setEcpm(calculateMetric(Profit, dspSlotHourDO.getShowUv(), 1000));
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    dspSlotHourDO.setEcprm(calculateMetric(Profit, dspSlotHourDO.getReqPv(), 1000000));
            } else {
                dspSlotHourDO.setEcpm(0.0);
                dspSlotHourDO.setEcprm(0.0);
            }
            if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                dspSlotHourDO.setMediaEcpm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getShowUv(), 1000));
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                dspSlotHourDO.setMediaEcprm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getReqPv(), 1000000));
            } else {
                dspSlotHourDO.setMediaEcpm(0.0);
                dspSlotHourDO.setMediaEcprm(0.0);
            }
        });


        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }


    @Override
    public PageResult<DspSlotHourRespExecVo> getDspSlotHourExceVo(DspSlotHourPageReqVO pageReqVO) {
        // 复用分页查询（已 JOIN 出 dspName/companyName/productName/mediaName/sspName/appName/osType）
        Page<DspSlotHourDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<DspSlotHourDO> resultPage = dspSlotHourMapper.selectDspSlotHourPage(page, pageReqVO, "hour1.date DESC");
        // 计算派生指标（填充率/展现率/点击率/ecpm 等）—— 与 getDspSlotHourPage 一致
        resultPage.getRecords().forEach(this::enrichDerivedMetrics);
        // 转换为导出 VO
        List<DspSlotHourRespExecVo> list = BeanUtils.toBean(resultPage.getRecords(), DspSlotHourRespExecVo.class);
        return new PageResult<>(list, resultPage.getTotal());
    }

    /**
     * 填充派生指标：填充率、展现率、点击率、ecpm、媒体ecpm、ecprm、媒体ecprm
     * 规则与 getDspSlotHourPage 一致
     */
    private void enrichDerivedMetrics(DspSlotHourDO dspSlotHourDO) {

        Long reqCount = dspSlotHourDO.getReqPv();
        if (reqCount == null || reqCount == 0) {
            return;
        }

        // 填充率=返回/请求*100%
        Long retPv = dspSlotHourDO.getRetPv();
        dspSlotHourDO.setFillRate(retPv != null && reqCount != 0
                ? formatRate(retPv * 100.0 / reqCount)
                : 0.0);

        // 展现率=展示/返回*100%
        Long showPv = dspSlotHourDO.getShowPv();
        if (retPv != null && retPv != 0) {
            dspSlotHourDO.setDisplayRate(showPv != null
                    ? formatRate(showPv * 100.0 / retPv)
                    : 0.0);
        } else {
            dspSlotHourDO.setDisplayRate(0.0);
        }

        // 点击率=点击/展示*100%
        Long clickPv = dspSlotHourDO.getClickPv();
        if (showPv != null && showPv != 0) {
            dspSlotHourDO.setClickRate(clickPv != null
                    ? formatRate(clickPv * 100.0 / showPv)
                    : 0.0);
        } else {
            dspSlotHourDO.setClickRate(0.0);
        }


        // ecpm（预算千次展示收益）=收益/展示*1000
        // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
        // ecprm（预算百万请求收益）=收益/请求*1000000
        // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000

        if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal Profit = dspSlotHourDO.getSpend().add(dspSlotHourDO.getIncome());

            // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
            dspSlotHourDO.setEcpm(calculateMetric(Profit, dspSlotHourDO.getShowPv(), 1000));
            // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
            dspSlotHourDO.setEcprm(calculateMetric(Profit, dspSlotHourDO.getReqPv(), 1000000));
        } else {
            dspSlotHourDO.setEcpm(0.0);
            dspSlotHourDO.setEcprm(0.0);
        }
        if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
            // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
            dspSlotHourDO.setMediaEcpm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getShowPv(), 1000));
            // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
            dspSlotHourDO.setMediaEcprm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getReqPv(), 1000000));
        } else {
            dspSlotHourDO.setMediaEcpm(0.0);
            dspSlotHourDO.setMediaEcprm(0.0);
        }

    }


    private double formatRate(double value) {
        return BigDecimal.valueOf(value)
                .setScale(2, RoundingMode.HALF_UP)
                .doubleValue();
    }

    // 使用 BigDecimal 计算指标并统一保留 4 位小数。
    private double calculateMetric(BigDecimal amount, Long requestCount, long multiplier) {
        return amount.multiply(BigDecimal.valueOf(multiplier))
                .divide(BigDecimal.valueOf(requestCount), 3, RoundingMode.HALF_UP)
                .doubleValue();
    }

    /**
     * 构建动态排序字段，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 将 camelCase 转为 snake_case，并校验合法字符防止 SQL 注入
     */
    private String buildSortField(List<SortingField> sortingFields) {
        if (sortingFields == null || sortingFields.isEmpty()) {
            return "hour1.date DESC";
        }
        SortingField sf = sortingFields.get(0);
        String field = sf.getField();
        if (field == null || field.isEmpty()) {
            return "hour1.date DESC";
        }
        // camelCase 转 snake_case
        String snake = field.replaceAll("([a-z])([A-Z])", "$1_$2").toLowerCase();
        // SQL 注入防护：仅允许字母、数字、下划线（MySQL 中合法列名）
        if (!snake.matches("^[a-zA-Z0-9_]+$")) {
            return "hour1.date DESC";
        }
        boolean asc = SortingField.ORDER_ASC.equalsIgnoreCase(sf.getOrder());
        return "hour1." + snake + (asc ? " ASC" : " DESC");
    }

    @Override
    public List<DspSlotHourDO> getDspSspSlotHour(Long sspSlotid, Integer date) {
        List<DspSlotHourDO> dspSspSlotHour = dspSlotHourMapper.getDspSspSlotHour(sspSlotid, date);
        for (DspSlotHourDO dspSlotHourDO : dspSspSlotHour) {

            Long reqCount = dspSlotHourDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return null;
            }
            // 填充率=返回/请求*100%
            Long retPv = dspSlotHourDO.getRetPv();
            dspSlotHourDO.setFillRate(retPv != null && reqCount != 0
                    ? formatRate(retPv * 100.0 / reqCount)
                    : 0.0);

            // 展现率=展示/返回*100%
            Long showPv = dspSlotHourDO.getShowPv();
            if (retPv != null && retPv != 0) {
                dspSlotHourDO.setDisplayRate(showPv != null
                        ? formatRate(showPv * 100.0 / retPv)
                        : 0.0);
            } else {
                dspSlotHourDO.setDisplayRate(0.0);
            }
            // 点击率=点击/展示*100%
            Long clickPv = dspSlotHourDO.getClickPv();
            if (showPv != null && showPv != 0) {
                dspSlotHourDO.setClickRate(clickPv != null
                        ? formatRate(clickPv * 100.0 / showPv)
                        : 0.0);
            } else {
                dspSlotHourDO.setClickRate(0.0);
            }

            if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal Profit = dspSlotHourDO.getSpend().add(dspSlotHourDO.getIncome());
                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                dspSlotHourDO.setEcpm(calculateMetric(Profit, dspSlotHourDO.getShowPv(), 1000));
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                dspSlotHourDO.setEcprm(calculateMetric(Profit, dspSlotHourDO.getReqPv(), 1000000));
            } else {
                dspSlotHourDO.setEcpm(0.0);
                dspSlotHourDO.setEcprm(0.0);
            }
            if (dspSlotHourDO.getSpend() != null && dspSlotHourDO.getSpend().compareTo(BigDecimal.ZERO) > 0) {
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                dspSlotHourDO.setMediaEcpm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getShowPv(), 1000));
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                dspSlotHourDO.setMediaEcprm(calculateMetric(dspSlotHourDO.getSpend(), dspSlotHourDO.getReqPv(), 1000000));
            } else {
                dspSlotHourDO.setMediaEcpm(0.0);
                dspSlotHourDO.setMediaEcprm(0.0);
            }
        }
            // 查看是否解绑
            for (DspSlotHourDO dspSlotHourDO1 : dspSspSlotHour) {
                List<LaunchDO> launchDOS = launchMapper.selectLaunchBySspSlotIdDspSlotId(sspSlotid, dspSlotHourDO1.getDspSlotId());
                if (launchDOS.size() > 0) {
                    dspSlotHourDO1.setIsDeleted(1);// 没有解绑
                } else {
                    dspSlotHourDO1.setIsDeleted(2); // 解绑
                }
            }

        return dspSspSlotHour;
    }

    @Override
    public List<DspSlotHourTrendRespVO> getDspSlotHourTrend(DspSlotHourPageReqVO pageReqVO) {
        // 按小时聚合查询原始指标（SQL 中 MOD(date,100) 已将 date 转为小时值 0~23）
        List<DspSlotHourDO> hourList = dspSlotHourMapper.selectHourTrend(pageReqVO);
        if (CollUtil.isEmpty(hourList)) {
            return Collections.emptyList();
        }

        List<DspSlotHourTrendRespVO> resultList = new ArrayList<>(hourList.size());
        for (DspSlotHourDO hour : hourList) {
            Long reqCount = hour.getReqPv() == null ? 0L : hour.getReqPv();
            Long retPv = hour.getRetPv() == null ? 0L : hour.getRetPv();
            Long showPv = hour.getShowPv() == null ? 0L : hour.getShowPv();
            Long clickPv = hour.getClickPv() == null ? 0L : hour.getClickPv();
            // 小时数据的成本、收入为 BigDecimal，保留原始精度返回趋势接口。
            BigDecimal spend = hour.getSpend() == null ? BigDecimal.ZERO : hour.getSpend();
            BigDecimal income = hour.getIncome() == null ? BigDecimal.ZERO : hour.getIncome();
            BigDecimal po = spend.subtract(income);
            // SQL 返回的 date 字段即为小时值（0~23）
            Integer hourValue = hour.getDate() == null ? 0 : hour.getDate();

            DspSlotHourTrendRespVO vo = DspSlotHourTrendRespVO.builder()
                    .hour(hourValue)
                    .reqPv(reqCount)
                    .retPv(retPv)
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
                    .ecpm(reqCount != 0 ? calculateMetric(po, showPv, 1000) : 0.0)
                    // 媒体ecpm = 成本 / 请求数 * 1000
                    .mediaEcpm(reqCount != 0 ? calculateMetric(spend, showPv, 1000) : 0.0)
                    // ecprm = 收入 / 请求数 * 1000000
                    .ecprm(reqCount != 0 ? calculateMetric(po, reqCount, 1000000) : 0.0)
                    // 媒体ecprm = 成本 / 请求数 * 1000000
                    .mediaEcprm(reqCount != 0 ? calculateMetric(spend, reqCount, 1000000) : 0.0)
                    .build();
            resultList.add(vo);
        }
        return resultList;
    }

}
