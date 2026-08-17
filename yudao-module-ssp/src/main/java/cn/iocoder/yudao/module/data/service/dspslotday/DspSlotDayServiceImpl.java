package cn.iocoder.yudao.module.data.service.dspslotday;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.SspSlotDayPageReqVO;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
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
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.SortingField;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_DAY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


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
            Long dspSlotId = dspSlotDayDO.getDspSlotId();
            Long sspSlotId = dspSlotDayDO.getSspSlotId();

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
                Long income = dspSlotDayDO.getIncome();
                if (income != null && income != 0) {
                    dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    dspSlotDayDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setEcpm(0L);
                    dspSlotDayDO.setEcprm(0L);
                }

                Long spend = dspSlotDayDO.getSpend();
                if (spend != null && spend != 0) {
                    dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setMediaEcpm(0L);
                    dspSlotDayDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = dspSlotDayDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    dspSlotDayDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setEcpm(0L);
                    dspSlotDayDO.setEcprm(0L);
                    dspSlotDayDO.setMediaEcpm(0L);
                    dspSlotDayDO.setMediaEcprm(0L);
                }
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
        Long dspSlotId = dspSlotDayDO.getDspSlotId();
        Long sspSlotId = dspSlotDayDO.getSspSlotId();

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
            Long income = dspSlotDayDO.getIncome();
            if (income != null && income != 0) {
                dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                dspSlotDayDO.setEcprm(income * 1000000 / reqCount);
            } else {
                dspSlotDayDO.setEcpm(0L);
                dspSlotDayDO.setEcprm(0L);
            }

            Long spend = dspSlotDayDO.getSpend();
            if (spend != null && spend != 0) {
                dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
            } else {
                dspSlotDayDO.setMediaEcpm(0L);
                dspSlotDayDO.setMediaEcprm(0L);
            }
        } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
            Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
            if (sspDealRatio == null) {
                sspDealRatio = 0;
            }
            Long spend = dspSlotDayDO.getSpend();
            if (spend != null && spend != 0 && sspDealRatio != 0) {
                // 预算收入 = 媒体成本 * 分成系数 / 100
                Long income = spend * sspDealRatio / 100;
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                dspSlotDayDO.setEcprm(income * 1000000 / reqCount);

                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
            } else {
                dspSlotDayDO.setEcpm(0L);
                dspSlotDayDO.setEcprm(0L);
                dspSlotDayDO.setMediaEcpm(0L);
                dspSlotDayDO.setMediaEcprm(0L);
            }
        }
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

    @Override
    public List<DspSlotDayDO> getSSPDspSlotDay(Long sspSlotid, int date) {
        List<DspSlotDayDO> sspDspSlotDay = dspSlotDayMapper.getSSPDspSlotDay(sspSlotid, date);
        for (DspSlotDayDO dspSlotDayDO : sspDspSlotDay) {
            Long dspSlotId = dspSlotDayDO.getDspSlotId();
            Long sspSlotId = dspSlotDayDO.getSspSlotId();
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
            Long reqCount = dspSlotDayDO.getReqPv();
            if (reqCount == null || reqCount == 0) {
                return null;
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

            // 预算RTB,媒体RTB
            if (dspPayType == 2 && sspPayType == 2) {
                Long income = dspSlotDayDO.getIncome();
                if (income != null && income != 0) {
                    dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    dspSlotDayDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setEcpm(0L);
                    dspSlotDayDO.setEcprm(0L);
                }

                Long spend = dspSlotDayDO.getSpend();
                if (spend != null && spend != 0) {
                    dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setMediaEcpm(0L);
                    dspSlotDayDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = dspSlotDayDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    dspSlotDayDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    dspSlotDayDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    dspSlotDayDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    dspSlotDayDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotDayDO.setEcpm(0L);
                    dspSlotDayDO.setEcprm(0L);
                    dspSlotDayDO.setMediaEcpm(0L);
                    dspSlotDayDO.setMediaEcprm(0L);
                }
            }


        }

        return sspDspSlotDay;
    }

    @Override
    public DspSlotDayRespVO getDspSlotDaySum(Long date) {
        // 查询指定日期所有记录
        List<DspSlotDayDO> list = dspSlotDayMapper.selectList(
                new LambdaQueryWrapperX<DspSlotDayDO>().eq(DspSlotDayDO::getDate, date));

        if (CollUtil.isEmpty(list)) {
            return new DspSlotDayRespVO();
        }

        // 对所有字段求和
        long sumShowPv = 0, sumShowUv = 0, sumClickPv = 0, sumClickUv = 0;
        long sumReqPv = 0, sumReqUv = 0, sumDiscard = 0, sumRetPv = 0, sumRetUv = 0;
        long sumSpend = 0, sumIncome = 0;
        long sumDiscountClickPv = 0, sumDiscountShowPv = 0;
        long sumDplsuccPv = 0, sumCompletePv = 0, sumInstallPv = 0, sumActivatePv = 0;

        for (DspSlotDayDO row : list) {
            sumShowPv += nullToZero(row.getShowPv());
            sumShowUv += nullToZero(row.getShowUv());
            sumClickPv += nullToZero(row.getClickPv());
            sumClickUv += nullToZero(row.getClickUv());
            sumReqPv += nullToZero(row.getReqPv());
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

        DspSlotDayRespVO result = new DspSlotDayRespVO();
        result.setShowPv(sumShowPv);
        result.setShowUv(sumShowUv);
        result.setClickPv(sumClickPv);
        result.setClickUv(sumClickUv);
        result.setReqPv(sumReqPv);
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

        // 计算派生指标
        // 填充率 = 返回PV / 请求PV * 100%
        result.setFillRate(sumRetPv != 0 ? formatRate(sumRetPv * 100.0 / sumReqPv) : 0.0);

        // 展现率 = 展示PV / 返回PV * 100%
        result.setDisplayRate(sumRetPv != 0 ? formatRate(sumShowPv * 100.0 / sumRetPv) : 0.0);

        // 点击率 = 点击PV / 展示PV * 100%
        result.setClickRate(sumShowPv != 0 ? formatRate(sumClickPv * 100.0 / sumShowPv) : 0.0);

        // ecpm（预算千次展示收益）= 收入 / 请求PV * 1000
        result.setEcpm(sumReqPv != 0 ? (double) (sumIncome * 1000 / sumReqPv) : 0.0);

        // 媒体ecpm（媒体千次展示收益）= 成本 / 请求PV * 1000
        result.setMediaEcpm(sumReqPv != 0 ? (double) (sumSpend * 1000 / sumReqPv) : 0.0);

        // ecprm（预算百万请求收益）= 收入 / 请求PV * 1000000
        result.setEcprm(sumReqPv != 0 ? (double) (sumIncome * 1000000 / sumReqPv) : 0.0);

        // 媒体ecprm（媒体百万请求收益）= 成本 / 请求PV * 1000000
        result.setMediaEcprm(sumReqPv != 0 ? (double) (sumSpend * 1000000 / sumReqPv) : 0.0);

        return result;
    }

    @Override
    public PageResult<DspSlotDayDO> getDspSlotDayPageDetail(DspSlotDayPageReqVO pageReqVO) {
        PageResult<DspSlotDayDO> dspSlotDayPageDetail = dspSlotDayMapper.getDspSlotDayPageDetail(pageReqVO);

        return null;
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

}