package cn.iocoder.yudao.module.dsp.dal.mysql.company;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.dsp.controller.admin.company.vo.*;

/**
 * 预算广告 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface CompanyMapper extends BaseMapperX<CompanyDO> {

    default PageResult<CompanyDO> selectPage(CompanyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<CompanyDO>()
                .likeIfPresent(CompanyDO::getName, reqVO.getName())
                .eqIfPresent(CompanyDO::getMethod, reqVO.getMethod())
                .betweenIfPresent(CompanyDO::getCreateTime, reqVO.getCreateTime())
                .betweenIfPresent(CompanyDO::getUpdateTime, reqVO.getUpdateTime())
                .orderByDesc(CompanyDO::getId));
    }

    default boolean existsByDspCode(Long dspCode) {
        return selectCount(new LambdaQueryWrapperX<CompanyDO>()
                .eq(CompanyDO::getDspCode, dspCode)) > 0;
    }

    default Long selectMaxDspCode() {
        CompanyDO company = selectOne(new LambdaQueryWrapperX<CompanyDO>()
                .orderByDesc(CompanyDO::getDspCode)
                .last("limit 1"));
        return company != null && company.getDspCode() != null ? company.getDspCode() : 0L;
    }

}