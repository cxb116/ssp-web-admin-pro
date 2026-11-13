package cn.iocoder.yudao.module.domain.dal.mysql.domain;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.domain.dal.dataobject.domain.DomainDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.domain.controller.admin.domain.vo.*;

/**
 * DSP域名管理 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DomainMapper extends BaseMapperX<DomainDO> {

    default PageResult<DomainDO> selectPage(DomainPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DomainDO>()
                .eqIfPresent(DomainDO::getDspSlotId, reqVO.getDspSlotId())
                .likeIfPresent(DomainDO::getDomainName, reqVO.getDomainName())
                .likeIfPresent(DomainDO::getDspName, reqVO.getDspName())
                .betweenIfPresent(DomainDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DomainDO::getId));
    }

}