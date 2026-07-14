package cn.iocoder.yudao.module.dsp.service.product;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.common.exception.ErrorCode;
import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.dsp.controller.admin.product.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.product.ProductDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.product.ProductMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.PRODUCT_HAS_COMPANY;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.PRODUCT_HAS_DSP_SLOT;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.PRODUCT_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 预算产品 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class ProductServiceImpl implements ProductService {

    @Resource
    private ProductMapper productMapper;

    @Resource
    private DspSlotInfoMapper slotInfoMapper;

    @Resource
    private EtcdClient etcdClient;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createProduct(ProductSaveReqVO createReqVO) {
        // 插入
        ProductDO product = BeanUtils.toBean(createReqVO, ProductDO.class);
        productMapper.insert(product);

        // 同步到etcd
        syncToEtcd(product);

        // 返回
        return product.getId();
    }

    @Override
    public void updateProduct(ProductSaveReqVO updateReqVO) {
        // 校验存在
        validateProductExists(updateReqVO.getId());
        // 更新
        ProductDO updateObj = BeanUtils.toBean(updateReqVO, ProductDO.class);
        productMapper.updateById(updateObj);

        // 同步到etcd
        syncToEtcd(updateObj);
    }

    @Override
    public void deleteProduct(Long id) {
        // 校验存在
        ProductDO product = validateProductExists(id);
        validateProductCanDelete(product);
        // 删除
        productMapper.deleteById(id);

        // 从etcd删除
        deleteFromEtcd(id);
    }

    @Override
        public void deleteProductListByIds(List<Long> ids) {
        validateProductCanDelete(ids);
        // 删除
        productMapper.deleteByIds(ids);

        // 批量从etcd删除
        for (Long id : ids) {
            deleteFromEtcd(id);
        }
        }


    private ProductDO validateProductExists(Long id) {
        ProductDO product = productMapper.selectById(id);
        if (product == null) {
            throw exception(PRODUCT_NOT_EXISTS);
        }
        return product;
    }

    private void validateProductCanDelete(ProductDO product) {
        if (product.getCompanyId() != null) {
            throw exception(PRODUCT_HAS_COMPANY);
        }
        validateProductHasNoDspSlot(Collections.singletonList(product.getId()));
    }

    private void validateProductCanDelete(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        for (Long id : ids) {
            ProductDO product = validateProductExists(id);
            if (product.getCompanyId() != null) {
                throw exception(PRODUCT_HAS_COMPANY);
            }
        }
        validateProductHasNoDspSlot(ids);
    }

    private void validateProductHasNoDspSlot(List<Long> ids) {
        Long count = slotInfoMapper.selectCount(new LambdaQueryWrapperX<DspSlotInfoDO>()
                .in(DspSlotInfoDO::getProductId, ids));
        if (count != null && count > 0) {
            throw exception(PRODUCT_HAS_DSP_SLOT);
        }
    }

    @Override
    public ProductDO getProduct(Long id) {
        return productMapper.selectById(id);
    }

    @Override
    public PageResult<ProductDO> getProductPage(ProductPageReqVO pageReqVO) {
        Long offset = (pageReqVO.getPageNo() - 1L) * pageReqVO.getPageSize();
        List<ProductDO> list = productMapper.selectPage(pageReqVO, offset, pageReqVO.getPageSize());
        Long total = productMapper.selectPageCount(pageReqVO);
        return new PageResult<>(list, total);
    }

    /**
     * 同步数据到etcd
     *
     * @param product 产品信息
     */
    private void syncToEtcd(ProductDO product) {
        try {
            // 构建etcd key: {etcdPrefix}/product/{id}
            String etcdKey = etcdPrefix + "/product/" + product.getId();

            // 构建符合DSP API要求的JSON格式
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", product.getId());
            etcdData.put("name", product.getName() != null ? product.getName() : "");
            etcdData.put("company_id", product.getCompanyId() != null ? product.getCompanyId() : 0);

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.put(etcdKey, etcdValue);

            log.info("Product 同步到etcd成功, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("Product 同步到etcd失败, id: {}", product.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id 产品ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/product/" + id;
            etcdClient.delete(etcdKey);
            log.info("Product 从etcd删除成功, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("Product 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }

}
