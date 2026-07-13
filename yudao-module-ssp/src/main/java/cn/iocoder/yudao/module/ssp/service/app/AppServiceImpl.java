package cn.iocoder.yudao.module.ssp.service.app;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.module.ssp.controller.admin.app.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.app.AppDO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.ssp.dal.mysql.app.AppMapper;
import cn.iocoder.yudao.module.ssp.dal.mysql.sspSlotInfo.SspSlotInfoMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.APP_HAS_SSP_SLOT;
import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.APP_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * 媒体应用 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class AppServiceImpl implements AppService {

    @Resource
    private AppMapper appMapper;

    @Resource
    private SspSlotInfoMapper slotInfoMapper;

    @Override
    public Long createApp(AppSaveReqVO createReqVO) {
        // 插入
        AppDO app = BeanUtils.toBean(createReqVO, AppDO.class);
        appMapper.insert(app);

        // 返回
        return app.getId();
    }

    @Override
    public void updateApp(AppSaveReqVO updateReqVO) {
        // 校验存在
        validateAppExists(updateReqVO.getId());
        // 更新
        AppDO updateObj = BeanUtils.toBean(updateReqVO, AppDO.class);
        appMapper.updateById(updateObj);
    }

    @Override
    public void deleteApp(Long id) {
        // 校验存在
        validateAppExists(id);
        validateAppHasNoSlot(Collections.singletonList(id));
        // 删除
        appMapper.deleteById(id);
    }

    @Override
        public void deleteAppListByIds(List<Long> ids) {
        validateAppHasNoSlot(ids);
        // 删除
        appMapper.deleteByIds(ids);
        }


    private void validateAppExists(Long id) {
        if (appMapper.selectAppById(id) == null) {
            throw exception(APP_NOT_EXISTS);
        }
    }

    private void validateAppHasNoSlot(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return;
        }
        List<SspSlotInfoDO> slots = slotInfoMapper.selectList(new LambdaQueryWrapperX<SspSlotInfoDO>()
                .in(SspSlotInfoDO::getAppId, ids));
        if (CollUtil.isNotEmpty(slots)) {
            throw exception(APP_HAS_SSP_SLOT);
        }
    }

    @Override
    public AppDO getApp(Long id) {
        return appMapper.selectAppById(id);
    }

    @Override
    public PageResult<AppDO> getAppPage(AppPageReqVO pageReqVO) {
        Long offset = (pageReqVO.getPageNo() - 1L) * pageReqVO.getPageSize();
        List<AppDO> list = appMapper.selectAppPage(pageReqVO, offset, pageReqVO.getPageSize());
        Long total = appMapper.selectAppPageCount(pageReqVO);
        return new PageResult<>(list, total);
    }

    @Override
    public List<AppDO> getAppMediaList(Long id) {
        return appMapper.getAppMediaList(id);
    }

}
