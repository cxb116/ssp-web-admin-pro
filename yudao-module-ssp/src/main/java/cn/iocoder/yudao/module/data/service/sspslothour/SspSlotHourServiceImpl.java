package cn.iocoder.yudao.module.data.service.sspslothour;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslothour.SspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.sspslothour.SspSlotHourMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SSP_SLOT_HOUR_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP-SSP广告位报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class SspSlotHourServiceImpl implements SspSlotHourService {

    @Resource
    private SspSlotHourMapper sspSlotHourMapper;

    @Override
    public Long createSspSlotHour(SspSlotHourSaveReqVO createReqVO) {
        // 插入
        SspSlotHourDO sspSlotHour = BeanUtils.toBean(createReqVO, SspSlotHourDO.class);
        sspSlotHourMapper.insert(sspSlotHour);

        // 返回
        return sspSlotHour.getId();
    }

    @Override
    public void updateSspSlotHour(SspSlotHourSaveReqVO updateReqVO) {
        // 校验存在
        validateSspSlotHourExists(updateReqVO.getId());
        // 更新
        SspSlotHourDO updateObj = BeanUtils.toBean(updateReqVO, SspSlotHourDO.class);
        sspSlotHourMapper.updateById(updateObj);
    }

    @Override
    public void deleteSspSlotHour(Long id) {
        // 校验存在
        validateSspSlotHourExists(id);
        // 删除
        sspSlotHourMapper.deleteById(id);
    }

    @Override
        public void deleteSspSlotHourListByIds(List<Long> ids) {
        // 删除
        sspSlotHourMapper.deleteByIds(ids);
        }


    private void validateSspSlotHourExists(Long id) {
        if (sspSlotHourMapper.selectById(id) == null) {
            throw exception(SSP_SLOT_HOUR_NOT_EXISTS);
        }
    }

    @Override
    public SspSlotHourDO getSspSlotHour(Long id) {
        return sspSlotHourMapper.selectById(id);
    }

    @Override
    public PageResult<SspSlotHourDO> getSspSlotHourPage(SspSlotHourPageReqVO pageReqVO) {
        return sspSlotHourMapper.selectPage(pageReqVO);
    }

}