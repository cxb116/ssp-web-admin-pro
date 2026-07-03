package cn.iocoder.yudao.module.data.service.dspslotday;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.dspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.dspslotday.DspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.module.data.dal.mysql.dspslotday.DspSlotDayMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.DSP_SLOT_DAY_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP预算广告位日期报 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class DspSlotDayServiceImpl implements DspSlotDayService {

    @Resource
    private DspSlotDayMapper dspSlotDayMapper;

    @Override
    public Long createDspSlotDay(DspSlotDaySaveReqVO createReqVO) {
        // 插入
        DspSlotDayDO dspSlotDay = BeanUtils.toBean(createReqVO, DspSlotDayDO.class);
        dspSlotDayMapper.insert(dspSlotDay);

        // 返回
        return dspSlotDay.getId();
    }

    @Override
    public void updateDspSlotDay(DspSlotDaySaveReqVO updateReqVO) {
        // 校验存在
        validateDspSlotDayExists(updateReqVO.getId());
        // 更新
        DspSlotDayDO updateObj = BeanUtils.toBean(updateReqVO, DspSlotDayDO.class);
        dspSlotDayMapper.updateById(updateObj);
    }

    @Override
    public void deleteDspSlotDay(Long id) {
        // 校验存在
        validateDspSlotDayExists(id);
        // 删除
        dspSlotDayMapper.deleteById(id);
    }

    @Override
        public void deleteDspSlotDayListByIds(List<Long> ids) {
        // 删除
        dspSlotDayMapper.deleteByIds(ids);
        }


    private void validateDspSlotDayExists(Long id) {
        if (dspSlotDayMapper.getDspSlotDay(id) == null) {
            throw exception(DSP_SLOT_DAY_NOT_EXISTS);
        }
    }

    @Override
    public DspSlotDayDO getDspSlotDay(Long id) {
        return dspSlotDayMapper.getDspSlotDay(id);
    }

    @Override
    public PageResult<DspSlotDayDO> getDspSlotDayPage(DspSlotDayPageReqVO pageReqVO) {
        Page<DspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<DspSlotDayDO> resultPage = dspSlotDayMapper.selectDspSlotDayPage(page, pageReqVO);
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public List<DspSlotDayDO> getSSPDspSlotDay(Long sspSlotId, int date) {
        List<DspSlotDayDO> sspDspSlotDay = dspSlotDayMapper.getSSPDspSlotDay(sspSlotId, date);
        return sspDspSlotDay;
    }

}