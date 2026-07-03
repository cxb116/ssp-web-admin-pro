package cn.iocoder.yudao.module.data.service.sspslotday;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.sspslotday.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.sspslotday.SspSlotDayDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import cn.iocoder.yudao.module.data.dal.mysql.sspslotday.SspSlotDayMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.SSP_SLOT_DAY_NOT_EXISTS;
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
public class SspSlotDayServiceImpl implements SspSlotDayService {

    @Resource
    private SspSlotDayMapper sspSlotDayMapper;

    @Override
    public Long createSspSlotDay(SspSlotDaySaveReqVO createReqVO) {
        // 插入
        SspSlotDayDO sspSlotDay = BeanUtils.toBean(createReqVO, SspSlotDayDO.class);
        sspSlotDayMapper.insert(sspSlotDay);

        // 返回
        return sspSlotDay.getId();
    }

    @Override
    public void updateSspSlotDay(SspSlotDaySaveReqVO updateReqVO) {
        // 校验存在
        validateSspSlotDayExists(updateReqVO.getId());
        // 更新
        SspSlotDayDO updateObj = BeanUtils.toBean(updateReqVO, SspSlotDayDO.class);
        sspSlotDayMapper.updateById(updateObj);
    }

    @Override
    public void deleteSspSlotDay(Long id) {
        // 校验存在
        validateSspSlotDayExists(id);
        // 删除
        sspSlotDayMapper.deleteById(id);
    }

    @Override
        public void deleteSspSlotDayListByIds(List<Long> ids) {
        // 删除
        sspSlotDayMapper.deleteByIds(ids);
        }


    private void validateSspSlotDayExists(Long id) {
        if (sspSlotDayMapper.getSspSlotDay(id) == null) {
            throw exception(SSP_SLOT_DAY_NOT_EXISTS);
        }
    }

    @Override
    public SspSlotDayDO getSspSlotDay(Long id) {
        return sspSlotDayMapper.getSspSlotDay(id);
    }

    @Override
    public PageResult<SspSlotDayDO> getSspSlotDayPage(SspSlotDayPageReqVO pageReqVO) {
        Page<SspSlotDayDO> page = new Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        Page<SspSlotDayDO> resultPage = sspSlotDayMapper.selectSspSlotDayPage(page, pageReqVO);
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public List<SspSlotDayDO> getDspSspSlotDay(Long dspSlotId,Long date) {
        return sspSlotDayMapper.getDspSspSlotDay(dspSlotId,date);
    }

}