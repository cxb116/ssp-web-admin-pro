package cn.iocoder.yudao.module.dsp.service.company;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.json.JSONUtil;
import cn.iocoder.yudao.framework.etcd.client.EtcdClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;

import java.util.*;
import cn.iocoder.yudao.module.dsp.controller.admin.company.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.dsp.dal.mysql.company.CompanyMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.COMPANY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.COMPANY_DSP_CODE_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 预算广告 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
@Slf4j
public class CompanyServiceImpl implements CompanyService {

    @Resource
    private CompanyMapper companyMapper;

    @Resource
    private EtcdClient etcdClient;

    @Value("${yudao.etcd.dsp.prefix:/dsp/config}")
    private String etcdPrefix;

    @Override
    public Long createCompany(CompanySaveReqVO createReqVO) {
        // 自动生成匹配值：取当前最大dspCode + 1，初始值为1
        if (createReqVO.getDspCode() == null) {
            Long maxDspCode = companyMapper.selectMaxDspCode();
            createReqVO.setDspCode(maxDspCode + 1);
        }
        if (companyMapper.existsByDspCode(createReqVO.getDspCode())) {
            throw exception(COMPANY_DSP_CODE_EXISTS);
        }
        // 插入
        CompanyDO company = BeanUtils.toBean(createReqVO, CompanyDO.class);
        companyMapper.insert(company);

        // 同步到etcd
        syncToEtcd(company);

        // 返回
        return company.getId();
    }

    @Override
    public void updateCompany(CompanySaveReqVO updateReqVO) {
        // 校验存在
        validateCompanyExists(updateReqVO.getId());
        // 更新
        CompanyDO updateObj = BeanUtils.toBean(updateReqVO, CompanyDO.class);
        companyMapper.updateById(updateObj);

        // 同步到etcd
        syncToEtcd(updateObj);
    }

    @Override
    public void deleteCompany(Long id) {
        // 校验存在
        validateCompanyExists(id);
        // 删除
        companyMapper.deleteById(id);

        // 从etcd删除
        deleteFromEtcd(id);
    }

    @Override
        public void deleteCompanyListByIds(List<Long> ids) {
        // 删除
        companyMapper.deleteByIds(ids);

        // 批量从etcd删除
        for (Long id : ids) {
            deleteFromEtcd(id);
        }
        }


    private void validateCompanyExists(Long id) {
        if (companyMapper.selectById(id) == null) {
            throw exception(COMPANY_NOT_EXISTS);
        }
    }

    @Override
    public CompanyDO getCompany(Long id) {
        return companyMapper.selectById(id);
    }

    @Override
    public PageResult<CompanyDO> getCompanyPage(CompanyPageReqVO pageReqVO) {
        return companyMapper.selectPage(pageReqVO);
    }

    /**
     * 同步数据到etcd
     *
     * @param company 公司信息
     */
    private void syncToEtcd(CompanyDO company) {
        try {
            // 构建etcd key: {etcdPrefix}/company/{id}
            String etcdKey = etcdPrefix + "/company/" + company.getId();

            // 构建符合DSP API要求的JSON格式
            Map<String, Object> etcdData = new HashMap<>();
            etcdData.put("id", company.getId());
            etcdData.put("name", company.getName());
            etcdData.put("dsp_code", company.getDspCode() != null ? company.getDspCode() : 0);
            etcdData.put("url", company.getUrl() != null ? company.getUrl() : "");
            etcdData.put("method", company.getMethod() != null && company.getMethod() == 1 ? "POST" : "GET");
            etcdData.put("timeout", company.getTimeout() != null ? company.getTimeout() * 1000000000L : 3000000000L);

            String etcdValue = JSONUtil.toJsonStr(etcdData);

            // 写入etcd
            etcdClient.put(etcdKey, etcdValue);

            log.info("Company 同步到etcd成功, key: {}, value: {}", etcdKey, etcdValue);
        } catch (Exception e) {
            log.error("Company 同步到etcd失败, id: {}", company.getId(), e);
            // 不抛出异常，避免影响数据库操作
        }
    }

    /**
     * 从etcd删除数据
     *
     * @param id 公司ID
     */
    private void deleteFromEtcd(Long id) {
        try {
            String etcdKey = etcdPrefix + "/company/" + id;
            etcdClient.delete(etcdKey);
            log.info("Company 从etcd删除成功, key: {}", etcdKey);
        } catch (Exception e) {
            log.error("Company 从etcd删除失败, id: {}", id, e);
            // 不抛出异常，避免影响数据库操作
        }
    }

}