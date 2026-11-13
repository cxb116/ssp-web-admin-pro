package cn.iocoder.yudao.module.domain.dal.mysql.domaincompany;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaincompany.DomainCompanyDO;
import org.apache.ibatis.annotations.Mapper;
import cn.iocoder.yudao.module.domain.controller.admin.domaincompany.vo.*;

/**
 * DSP域名公司列 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DomainCompanyMapper extends BaseMapperX<DomainCompanyDO> {

    default PageResult<DomainCompanyDO> selectPage(DomainCompanyPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DomainCompanyDO>()
                .eqIfPresent(DomainCompanyDO::getDspCompanyId, reqVO.getDspCompanyId())
                .likeIfPresent(DomainCompanyDO::getDomainName, reqVO.getDomainName())
                // 前端改为输入框检索，公司名按模糊匹配
                .likeIfPresent(DomainCompanyDO::getDspName, reqVO.getDspName())
                .betweenIfPresent(DomainCompanyDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DomainCompanyDO::getId));
    }

}