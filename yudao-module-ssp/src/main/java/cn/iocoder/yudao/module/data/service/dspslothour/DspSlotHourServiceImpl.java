package cn.iocoder.yudao.module.data.service.dspslothour;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslothour.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslothour.DspSlotHourDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.dspslothour.DspSlotHourMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_HOUR_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP预算广告位小时报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DspSlotHourServiceImpl implements DspSlotHourService {

    @Resource
    private DspSlotHourMapper dspSlotHourMapper;

    @Override
    public Long createDspSlotHour(DspSlotHourSaveReqVO createReqVO) {
        // 插入
        DspSlotHourDO dspSlotHour = BeanUtils.toBean(createReqVO, DspSlotHourDO.class);
        dspSlotHourMapper.insert(dspSlotHour);

        // 返回
        return dspSlotHour.getId();
    }

    @Override
    public void updateDspSlotHour(DspSlotHourSaveReqVO updateReqVO) {
        // 校验存在
        validateDspSlotHourExists(updateReqVO.getId());
        // 更新
        DspSlotHourDO updateObj = BeanUtils.toBean(updateReqVO, DspSlotHourDO.class);
        dspSlotHourMapper.updateById(updateObj);
    }

    @Override
    public void deleteDspSlotHour(Long id) {
        // 校验存在
        validateDspSlotHourExists(id);
        // 删除
        dspSlotHourMapper.deleteById(id);
    }

    @Override
        public void deleteDspSlotHourListByIds(List<Long> ids) {
        // 删除
        dspSlotHourMapper.deleteByIds(ids);
        }


    private void validateDspSlotHourExists(Long id) {
        if (dspSlotHourMapper.selectById(id) == null) {
            throw exception(DSP_SLOT_HOUR_NOT_EXISTS);
        }
    }

    @Override
    public DspSlotHourDO getDspSlotHour(Long id) {
        return dspSlotHourMapper.selectById(id);
    }

    @Override
    public PageResult<DspSlotHourDO> getDspSlotHourPage(DspSlotHourPageReqVO pageReqVO) {
        return dspSlotHourMapper.selectPage(pageReqVO);
    }

}