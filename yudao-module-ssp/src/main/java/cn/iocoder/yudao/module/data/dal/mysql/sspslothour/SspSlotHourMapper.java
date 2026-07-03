package cn.iocoder.yudao.module.data.dal.mysql.sspslothour;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;

/**
 * DSP-SSP广告位报 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SspSlotHourMapper extends BaseMapperX<SspSlotHourDO> {

    default PageResult<SspSlotHourDO> selectPage(SspSlotHourPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SspSlotHourDO>()
                .eqIfPresent(SspSlotHourDO::getMediaId, reqVO.getMediaId())
                .eqIfPresent(SspSlotHourDO::getAppId, reqVO.getAppId())
                .eqIfPresent(SspSlotHourDO::getSspSlotId, reqVO.getSspSlotId())
                .eqIfPresent(SspSlotHourDO::getDspSlotId, reqVO.getDspSlotId())
                .eqIfPresent(SspSlotHourDO::getDspSlotCode, reqVO.getDspSlotCode())
                .orderByDesc(SspSlotHourDO::getId));
    }

}