package cn.iocoder.yudao.module.ssp.dal.mysql.slotblacklistdomain;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklistdomain.SlotBlacklistDomainDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.ssp.controller.admin.slotblacklistdomain.vo.*;

/**
 * 黑名单统计 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface SlotBlacklistDomainMapper extends BaseMapperX<SlotBlacklistDomainDO> {

    default PageResult<SlotBlacklistDomainDO> selectPage(SlotBlacklistDomainPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<SlotBlacklistDomainDO>()
                .eqIfPresent(SlotBlacklistDomainDO::getMediaId, reqVO.getMediaId())
                .eqIfPresent(SlotBlacklistDomainDO::getDomain, reqVO.getDomain())
                .betweenIfPresent(SlotBlacklistDomainDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(SlotBlacklistDomainDO::getId));
    }

}