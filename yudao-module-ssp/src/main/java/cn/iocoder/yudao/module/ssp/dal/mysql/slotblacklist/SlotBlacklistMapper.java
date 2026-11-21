package cn.iocoder.yudao.module.ssp.dal.mysql.slotblacklist;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklist.SlotBlacklistDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklist.vo.*;

/**
 * 黑名单配置 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SlotBlacklistMapper extends BaseMapperX<SlotBlacklistDO> {

    default PageResult<SlotBlacklistDO> selectPage(SlotBlacklistPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SlotBlacklistDO>()
                .eqIfPresent(SlotBlacklistDO::getMediaId, reqVO.getMediaId())
                .eqIfPresent(SlotBlacklistDO::getBacklistDomains, reqVO.getBacklistDomains())
                .eqIfPresent(SlotBlacklistDO::getStatus, reqVO.getStatus())
                .betweenIfPresent(SlotBlacklistDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SlotBlacklistDO::getId));
    }

}