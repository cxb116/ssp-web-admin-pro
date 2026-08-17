package cn.iocoder.yudao.module.data.service.dspslothour;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
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
            Long dspSlotId = dspSlotHourDO.getDspSlotId();
            Long sspSlotId = dspSlotHourDO.getSspSlotId();

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

            // 预算RTB,媒体RTB
            if (dspPayType == 2 && sspPayType == 2) {
                Long income = dspSlotHourDO.getIncome();
                if (income != null && income != 0) {
                    dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    dspSlotHourDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setEcpm(0L);
                    dspSlotHourDO.setEcprm(0L);
                }

                Long spend = dspSlotHourDO.getSpend();
                if (spend != null && spend != 0) {
                    dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setMediaEcpm(0L);
                    dspSlotHourDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = dspSlotHourDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    dspSlotHourDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setEcpm(0L);
                    dspSlotHourDO.setEcprm(0L);
                    dspSlotHourDO.setMediaEcpm(0L);
                    dspSlotHourDO.setMediaEcprm(0L);
                }
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
        Long dspSlotId = dspSlotHourDO.getDspSlotId();
        Long sspSlotId = dspSlotHourDO.getSspSlotId();

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
            Long income = dspSlotHourDO.getIncome();
            if (income != null && income != 0) {
                dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                dspSlotHourDO.setEcprm(income * 1000000 / reqCount);
            } else {
                dspSlotHourDO.setEcpm(0L);
                dspSlotHourDO.setEcprm(0L);
            }

            Long spend = dspSlotHourDO.getSpend();
            if (spend != null && spend != 0) {
                dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
            } else {
                dspSlotHourDO.setMediaEcpm(0L);
                dspSlotHourDO.setMediaEcprm(0L);
            }
        } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
            Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
            if (sspDealRatio == null) {
                sspDealRatio = 0;
            }
            Long spend = dspSlotHourDO.getSpend();
            if (spend != null && spend != 0 && sspDealRatio != 0) {
                // 预算收入 = 媒体成本 * 分成系数 / 100
                Long income = spend * sspDealRatio / 100;
                // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                dspSlotHourDO.setEcprm(income * 1000000 / reqCount);

                // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
            } else {
                dspSlotHourDO.setEcpm(0L);
                dspSlotHourDO.setEcprm(0L);
                dspSlotHourDO.setMediaEcpm(0L);
                dspSlotHourDO.setMediaEcprm(0L);
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
            Long dspSlotId = dspSlotHourDO.getDspSlotId();
            Long sspSlotId = dspSlotHourDO.getSspSlotId();

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

            // 预算RTB,媒体RTB
            if (dspPayType == 2 && sspPayType == 2) {
                Long income = dspSlotHourDO.getIncome();
                if (income != null && income != 0) {
                    dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    dspSlotHourDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setEcpm(0L);
                    dspSlotHourDO.setEcprm(0L);
                }

                Long spend = dspSlotHourDO.getSpend();
                if (spend != null && spend != 0) {
                    dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setMediaEcpm(0L);
                    dspSlotHourDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = dspSlotHourDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    dspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    dspSlotHourDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    dspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    dspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    dspSlotHourDO.setEcpm(0L);
                    dspSlotHourDO.setEcprm(0L);
                    dspSlotHourDO.setMediaEcpm(0L);
                    dspSlotHourDO.setMediaEcprm(0L);
                }
            }

        }
        return dspSspSlotHour;
    }

}