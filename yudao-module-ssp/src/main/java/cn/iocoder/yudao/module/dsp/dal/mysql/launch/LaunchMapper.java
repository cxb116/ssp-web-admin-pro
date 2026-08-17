package cn.iocoder.yudao.module.dsp.dal.mysql.launch;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.dsp.controller.admin.launch.vo.*;

/**
 * 媒体预算绑定 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface LaunchMapper extends BaseMapperX<LaunchDO> {

    default PageResult<LaunchDO> selectPage(LaunchPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<LaunchDO>()
                .eqIfPresent(LaunchDO::getSspSlotId, reqVO.getSspSlotId())
                .eqIfPresent(LaunchDO::getDspSlotId, reqVO.getDspSlotId())
                .eqIfPresent(LaunchDO::getTrafficWeight, reqVO.getTrafficWeight())
                .eqIfPresent(LaunchDO::getTrafficGroup, reqVO.getTrafficGroup())
                .eqIfPresent(LaunchDO::getFloorPrice, reqVO.getFloorPrice())
                .eqIfPresent(LaunchDO::getDspPayRatio, reqVO.getDspPayRatio())
                .betweenIfPresent(LaunchDO::getLaunchTime, reqVO.getLaunchTime())
                .eqIfPresent(LaunchDO::getLaunchHour, reqVO.getLaunchHour())
                .betweenIfPresent(LaunchDO::getLogTime, reqVO.getLogTime())
                .eqIfPresent(LaunchDO::getReq, reqVO.getReq())
                .eqIfPresent(LaunchDO::getIms, reqVO.getIms())
                .eqIfPresent(LaunchDO::getClk, reqVO.getClk())
                .eqIfPresent(LaunchDO::getPkgTrans, reqVO.getPkgTrans())
                .betweenIfPresent(LaunchDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(LaunchDO::getId));
    }

    /**
     * 根据 sspSlotId 查询 DSP 广告位列表
     */
    List<DspSlotInfoDO> selectDspSlotInfoBySspSlotId(Long sspSlotId);

    /**
     * 根据 sspSlotId 查询 Launch 列表
     */
    List<LaunchDO> selectLaunchBySspSlotId(Long sspSlotId);

    /**
     *  根据dspSlotId 查询 Launch 列表
     * @param id dspSlotId
     * @return
     */
    List<LaunchDO> selectLaunchByDspSlotId(Long id);

    /**
     * 根据 dspSlotId 查询 SSP 广告位列表
     */
    List<SspSlotInfoDO> selectSspSlotInfoByDspSlotId(Long dspSlotId);

    /**
     * 根据sspSlotId 去查 DspLaunch 的数据
     * @param id
     * @return
     */
    List<LaunchDO> getLaunchSspSlotList(Long id);

    /**
     * 查询绑定条数
     * @param sspSlotId
     * @param id
     * @return
     */
    List<LaunchDO> selectLaunchBySspSlotIdDspSlotId(@Param("sspSlotId") Long sspSlotId, @Param("dspSlotId") Long dspSlotId);
}