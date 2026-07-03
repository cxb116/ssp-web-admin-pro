package cn.iocoder.yudao.module.ssp.dal.mysql.media;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.ssp.dal.dataobject.media.MediaDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.ssp.controller.admin.media.vo.*;

/**
 * 媒体 Mapper
 *
 * @author 少年阿宾
 */
@Mapper
public interface MediaMapper extends BaseMapperX<MediaDO> {

    default PageResult<MediaDO> selectPage(MediaPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<MediaDO>()
                .eqIfPresent(MediaDO::getAccount, reqVO.getAccount())
                .likeIfPresent(MediaDO::getMediaCompanyShort, reqVO.getMediaCompanyShort())
                .eqIfPresent(MediaDO::getAccessType, reqVO.getAccessType())
                .eqIfPresent(MediaDO::getTrafficType, reqVO.getTrafficType())
                .eqIfPresent(MediaDO::getEnable, reqVO.getEnable())
                .betweenIfPresent(MediaDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(MediaDO::getUpdateTime, reqVO.getUpdateTime())
                .orderByDesc(MediaDO::getId));
    }

}