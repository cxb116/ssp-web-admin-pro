package cn.iocoder.yudao.module.dsp.dal.mysql.product;

import java.util.*;

import cn.iocoder.yudao.module.dsp.controller.admin.product.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.product.ProductDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;

/**
 * 预算产品 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface ProductMapper extends BaseMapperX<ProductDO> {

    /**
     * 分页查询产品列表（带公司名称）
     */
    List<ProductDO> selectPage(@Param("reqVO") ProductPageReqVO reqVO, @Param("offset") Long offset, @Param("pageSize") Integer pageSize);

    /**
     * 查询产品总数
     */
    Long selectPageCount(@Param("reqVO") ProductPageReqVO reqVO);

}