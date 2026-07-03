package cn.iocoder.yudao.module.data.dal.mysql.dspslothour;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;

/**
 * DSP预算广告位小时报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DspSlotHourMapper extends BaseMapperX<DspSlotHourDO> {

    default PageResult<DspSlotHourDO> selectPage(DspSlotHourPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DspSlotHourDO>()
                .eqIfPresent(DspSlotHourDO::getDspSlotId, reqVO.getDspSlotId())
                .eqIfPresent(DspSlotHourDO::getDspSlotCode, reqVO.getDspSlotCode())
                .eqIfPresent(DspSlotHourDO::getSspSlotId, reqVO.getSspSlotId())
                .eqIfPresent(DspSlotHourDO::getShowPv, reqVO.getShowPv())
                .eqIfPresent(DspSlotHourDO::getShowUv, reqVO.getShowUv())
                .eqIfPresent(DspSlotHourDO::getClickPv, reqVO.getClickPv())
                .eqIfPresent(DspSlotHourDO::getClickUv, reqVO.getClickUv())
                .eqIfPresent(DspSlotHourDO::getReqPv, reqVO.getReqPv())
                .eqIfPresent(DspSlotHourDO::getReqUv, reqVO.getReqUv())
                .eqIfPresent(DspSlotHourDO::getDiscard, reqVO.getDiscard())
                .eqIfPresent(DspSlotHourDO::getRetPv, reqVO.getRetPv())
                .eqIfPresent(DspSlotHourDO::getRetUv, reqVO.getRetUv())
                .eqIfPresent(DspSlotHourDO::getSpend, reqVO.getSpend())
                .eqIfPresent(DspSlotHourDO::getIncome, reqVO.getIncome())
                .eqIfPresent(DspSlotHourDO::getDiscountClickPv, reqVO.getDiscountClickPv())
                .eqIfPresent(DspSlotHourDO::getDiscountShowPv, reqVO.getDiscountShowPv())
                .eqIfPresent(DspSlotHourDO::getDplsuccPv, reqVO.getDplsuccPv())
                .eqIfPresent(DspSlotHourDO::getCompletePv, reqVO.getCompletePv())
                .eqIfPresent(DspSlotHourDO::getInstallPv, reqVO.getInstallPv())
                .eqIfPresent(DspSlotHourDO::getActivatePv, reqVO.getActivatePv())
                .betweenIfPresent(DspSlotHourDO::getDate, reqVO.getDate())
                .eqIfPresent(DspSlotHourDO::getCreatedAt, reqVO.getCreatedAt())
                .betweenIfPresent(DspSlotHourDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DspSlotHourDO::getId));
    }

}