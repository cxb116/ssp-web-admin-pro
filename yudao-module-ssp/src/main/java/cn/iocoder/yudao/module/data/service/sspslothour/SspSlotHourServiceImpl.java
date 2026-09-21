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
                            .divide(BigDecimal.valueOf(sspSlotHourDO.getReqUv()), 3, RoundingMode.HALF_UP).doubleValue());
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
                            .divide(BigDecimal.valueOf(sspSlotHourDO.getReqUv()), 3, RoundingMode.HALF_UP).doubleValue());

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

    /**
     * 构建动态排序字段，前端通过 buildSortingField 发送 sortingFields[0].field / sortingFields[0].order
     * 将 camelCase 转为 snake_case，并校验合法字符防止 SQL 注入
     */
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
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getReqUv()), 3, RoundingMode.HALF_UP).doubleValue());
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
                        .divide(BigDecimal.valueOf(sspSlotHourDO.getReqUv()), 3, RoundingMode.HALF_UP).doubleValue());
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
