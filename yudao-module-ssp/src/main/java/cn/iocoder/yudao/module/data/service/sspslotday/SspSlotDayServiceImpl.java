package cn.iocoder.yudao.module.data.service.sspslotday;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
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

        Page<SspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<SspSlotDayDO> resultPage = sspSlotDayMapper.selectSspSlotDayPage(page, pageReqVO);
        // 预算RTB,媒体分成
        resultPage.getRecords().forEach(sspSlotDayDO -> {
            Long dspSlotId = sspSlotDayDO.getDspSlotId();
            Long sspSlotId = sspSlotDayDO.getSspSlotId();

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

            // 查找预算DspSlotInfo 对象
            DspSlotInfoDO dspSlotInfoDO = dspSlotInfoMapper.selectById(dspSlotId);
            if (dspSlotInfoDO == null) {
                return;
            }
            // 上游预算结算方式，1=分成，2=RTB 3=固价
            Integer dspPayType = dspSlotInfoDO.getDspPayType();
            if (dspPayType == null) {
                return;
            }

            SspSlotInfoDO sspSlotInfoDO = sspSlotInfoMapper.selectById(sspSlotId);
            if (sspSlotInfoDO == null) {
                return;
            }
            // 下游媒体结算方式，1=分成，2=RTB, 3=固价
            Integer sspPayType = sspSlotInfoDO.getSspPayType();
            if (sspPayType == null) {
                return;
            }


            // ecpm（预算千次展示收益）=收益/请求*1000
            // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
            // ecprm（预算百万请求收益）=收益/请求*1000000
            // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000

            // 预算RTB,媒体RTB
            if (dspPayType == 2 && sspPayType == 2) {
                Long income = sspSlotDayDO.getIncome();
                if (income != null && income != 0) {
                    sspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    sspSlotDayDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setEcpm(0L);
                    sspSlotDayDO.setEcprm(0L);
                }

                Long spend = sspSlotDayDO.getSpend();
                if (spend != null && spend != 0) {
                    sspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    sspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setMediaEcpm(0L);
                    sspSlotDayDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = sspSlotDayDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    sspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    sspSlotDayDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    sspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    sspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setEcpm(0L);
                    sspSlotDayDO.setEcprm(0L);
                    sspSlotDayDO.setMediaEcpm(0L);
                    sspSlotDayDO.setMediaEcprm(0L);
                }
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

    @Override
    public List<SspSlotDayDO> getDspSspSlotDay(Long dspSlotId,Long date) {
        List<SspSlotDayDO> dspSspSlotDay = sspSlotDayMapper.getDspSspSlotDay(dspSlotId, date);
        for (SspSlotDayDO sspSlotDayDO : dspSspSlotDay) {
//            Long dspSlotIdOne = sspSlotDayDO.getDspSlotId();
            Long sspSlotId = sspSlotDayDO.getSspSlotId();

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

            // 查找预算DspSlotInfo 对象
            DspSlotInfoDO dspSlotInfoDO = dspSlotInfoMapper.selectById(dspSlotId);
            if (dspSlotInfoDO == null) {
                return null;
            }


            // 上游预算结算方式，1=分成，2=RTB 3=固价
            Integer dspPayType = dspSlotInfoDO.getDspPayType();
            if (dspPayType == null) {
                return null;
            }

            SspSlotInfoDO sspSlotInfoDO = sspSlotInfoMapper.selectById(sspSlotId);
            if (sspSlotInfoDO == null) {
                return null;
            }
            // 下游媒体结算方式，1=分成，2=RTB, 3=固价
            Integer sspPayType = sspSlotInfoDO.getSspPayType();
            if (sspPayType == null) {
                return null;
            }

            // ecpm（预算千次展示收益）=收益/请求*1000
            // 媒体ecpm（媒体千次展示收益）=成本/请求*1000
            // ecprm（预算百万请求收益）=收益/请求*1000000
            // 媒体ecprm（媒体百万请求收益）=成本/请求*1000000

            // 预算RTB,媒体RTB
            if (dspPayType == 2 && sspPayType == 2) {
                Long income = sspSlotDayDO.getIncome();
                if (income != null && income != 0) {
                    sspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    sspSlotDayDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setEcpm(0L);
                    sspSlotDayDO.setEcprm(0L);
                }

                Long spend = sspSlotDayDO.getSpend();
                if (spend != null && spend != 0) {
                    sspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    sspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setMediaEcpm(0L);
                    sspSlotDayDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = sspSlotDayDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    sspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    sspSlotDayDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    sspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    sspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotDayDO.setEcpm(0L);
                    sspSlotDayDO.setEcprm(0L);
                    sspSlotDayDO.setMediaEcpm(0L);
                    sspSlotDayDO.setMediaEcprm(0L);
                }
            }
        }

        return dspSspSlotDay;
    }

    @Override
    public SspSlotDayRespVO getSspSlotDaySum(Long date) {
        // 查询指定日期所有记录
        List<SspSlotDayDO> list = sspSlotDayMapper.selectList(
                new LambdaQueryWrapperX<SspSlotDayDO>().eq(SspSlotDayDO::getDate, date));

        if (CollUtil.isEmpty(list)) {
            return new SspSlotDayRespVO();
        }

        // 对所有字段求和
        long sumShowPv = 0, sumShowUv = 0, sumClickPv = 0, sumClickUv = 0;
        long sumReqPv = 0, sumReqCount = 0, sumReqUv = 0;
        long sumDiscard = 0, sumRetPv = 0, sumRetUv = 0;
        long sumSpend = 0, sumIncome = 0;
        long sumDiscountClickPv = 0, sumDiscountShowPv = 0;
        long sumDplsuccPv = 0, sumCompletePv = 0, sumInstallPv = 0, sumActivatePv = 0;

        for (SspSlotDayDO row : list) {
            sumShowPv += nullToZero(row.getShowPv());
            sumShowUv += nullToZero(row.getShowUv());
            sumClickPv += nullToZero(row.getClickPv());
            sumClickUv += nullToZero(row.getClickUv());
            sumReqPv += nullToZero(row.getReqPv());
            sumReqCount += nullToZero(row.getReqCount());
            sumReqUv += nullToZero(row.getReqUv());
            sumDiscard += nullToZero(row.getDiscard());
            sumRetPv += nullToZero(row.getRetPv());
            sumRetUv += nullToZero(row.getRetUv());
            sumSpend += nullToZero(row.getSpend());
            sumIncome += nullToZero(row.getIncome());
            sumDiscountClickPv += nullToZero(row.getDiscountClickPv());
            sumDiscountShowPv += nullToZero(row.getDiscountShowPv());
            sumDplsuccPv += nullToZero(row.getDplsuccPv());
            sumCompletePv += nullToZero(row.getCompletePv());
            sumInstallPv += nullToZero(row.getInstallPv());
            sumActivatePv += nullToZero(row.getActivatePv());
        }

        SspSlotDayRespVO result = new SspSlotDayRespVO();
        result.setShowPv(sumShowPv);
        result.setShowUv(sumShowUv);
        result.setClickPv(sumClickPv);
        result.setClickUv(sumClickUv);
        result.setReqPv(sumReqPv);
        result.setReqCount(sumReqCount);
        result.setReqUv(sumReqUv);
        result.setDiscard(sumDiscard);
        result.setRetPv(sumRetPv);
        result.setRetUv(sumRetUv);
        result.setSpend(sumSpend);
        result.setIncome(sumIncome);
        result.setDiscountClickPv(sumDiscountClickPv);
        result.setDiscountShowPv(sumDiscountShowPv);
        result.setDplsuccPv(sumDplsuccPv);
        result.setCompletePv(sumCompletePv);
        result.setInstallPv(sumInstallPv);
        result.setActivatePv(sumActivatePv);
        result.setDate(date);

        // 计算派生指标（分母使用 reqCount，与分页查询公式一致）
        // 填充率 = 返回PV / 请求数 * 100%
        result.setFillRate(sumReqCount != 0 ? formatRate(sumRetPv * 100.0 / sumReqCount) : 0.0);

        // 展现率 = 展示PV / 返回PV * 100%
        result.setDisplayRate(sumRetPv != 0 ? formatRate(sumShowPv * 100.0 / sumRetPv) : 0.0);

        // 点击率 = 点击PV / 展示PV * 100%
        result.setClickRate(sumShowPv != 0 ? formatRate(sumClickPv * 100.0 / sumShowPv) : 0.0);

        // ecpm（预算千次展示收益）= 收入 / 请求数 * 1000
        result.setEcpm(sumReqCount != 0 ? sumIncome * 1000 / sumReqCount : 0L);

        // 媒体ecpm（媒体千次展示收益）= 成本 / 请求数 * 1000
        result.setMediaEcpm(sumReqCount != 0 ? sumSpend * 1000 / sumReqCount : 0L);

        // ecprm（预算百万请求收益）= 收入 / 请求数 * 1000000
        result.setEcprm(sumReqCount != 0 ? sumIncome * 1000000 / sumReqCount : 0L);

        // 媒体ecprm（媒体百万请求收益）= 成本 / 请求数 * 1000000
        result.setMediaEcprm(sumReqCount != 0 ? sumSpend * 1000000 / sumReqCount : 0L);

        return result;
    }

    private long nullToZero(Long val) {
        return val == null ? 0L : val;
    }

}