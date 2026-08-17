package cn.iocoder.yudao.module.data.service.sspslothour;

import cn.hutool.core.collection.CollUtil;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
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
            Long dspSlotId = sspSlotHourDO.getDspSlotId();
            Long sspSlotId = sspSlotHourDO.getSspSlotId();

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
                Long income = sspSlotHourDO.getIncome();
                if (income != null && income != 0) {
                    sspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    sspSlotHourDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setEcpm(0L);
                    sspSlotHourDO.setEcprm(0L);
                }

                Long spend = sspSlotHourDO.getSpend();
                if (spend != null && spend != 0) {
                    sspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    sspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setMediaEcpm(0L);
                    sspSlotHourDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = sspSlotHourDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    sspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    sspSlotHourDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    sspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    sspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setEcpm(0L);
                    sspSlotHourDO.setEcprm(0L);
                    sspSlotHourDO.setMediaEcpm(0L);
                    sspSlotHourDO.setMediaEcprm(0L);
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
                Long income = sspSlotHourDO.getIncome();
                if (income != null && income != 0) {
                    sspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    sspSlotHourDO.setEcprm(income * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setEcpm(0L);
                    sspSlotHourDO.setEcprm(0L);
                }

                Long spend = sspSlotHourDO.getSpend();
                if (spend != null && spend != 0) {
                    sspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    sspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setMediaEcpm(0L);
                    sspSlotHourDO.setMediaEcprm(0L);
                }
            } else if (dspPayType == 2 && sspPayType == 1) { // 预算RTB,媒体分成
                Integer sspDealRatio = sspSlotInfoDO.getSspDealRatio(); // 媒体分成系数
                if (sspDealRatio == null) {
                    sspDealRatio = 0;
                }
                Long spend = sspSlotHourDO.getSpend();
                if (spend != null && spend != 0 && sspDealRatio != 0) {
                    // 预算收入 = 媒体成本 * 分成系数 / 100
                    Long income = spend * sspDealRatio / 100;
                    // ecpm（预算千次展示收益）= 收益 / 请求 * 1000
                    sspSlotHourDO.setEcpm(income * 1000 / reqCount);
                    // ecprm（预算百万请求收益）= 收益 / 请求 * 1000000
                    sspSlotHourDO.setEcprm(income * 1000000 / reqCount);

                    // 媒体ecpm（媒体千次展示收益）= 成本 / 请求 * 1000
                    sspSlotHourDO.setMediaEcpm(spend * 1000 / reqCount);
                    // 媒体ecprm（媒体百万请求收益）= 成本 / 请求 * 1000000
                    sspSlotHourDO.setMediaEcprm(spend * 1000000 / reqCount);
                } else {
                    sspSlotHourDO.setEcpm(0L);
                    sspSlotHourDO.setEcprm(0L);
                    sspSlotHourDO.setMediaEcpm(0L);
                    sspSlotHourDO.setMediaEcprm(0L);
                }
            }
        }

        return sspDspSlotHour;
    }

}