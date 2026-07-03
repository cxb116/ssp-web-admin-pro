package cn.iocoder.yudao.module.dsp.service.product;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.dsp.controller.admin.product.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.product.ProductDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * 预算产品 Service 接口
 *
 * @author 芋道源码
 */
public interface ProductService {

    /**
     * 创建预算产品
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createProduct(@Valid ProductSaveReqVO createReqVO);

    /**
     * 更新预算产品
     *
     * @param updateReqVO 更新信息
     */
    void updateProduct(@Valid ProductSaveReqVO updateReqVO);

    /**
     * 删除预算产品
     *
     * @param id 编号
     */
    void deleteProduct(Long id);

    /**
    * 批量删除预算产品
    *
    * @param ids 编号
    */
    void deleteProductListByIds(List<Long> ids);

    /**
     * 获得预算产品
     *
     * @param id 编号
     * @return 预算产品
     */
    ProductDO getProduct(Long id);

    /**
     * 获得预算产品分页
     *
     * @param pageReqVO 分页查询
     * @return 预算产品分页
     */
    PageResult<ProductDO> getProductPage(ProductPageReqVO pageReqVO);

}