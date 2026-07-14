package cn.iocoder.yudao.module.data.service.inputexec;

import cn.hutool.core.collection.CollUtil;
import org.springframework.stereotype.Service;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;

import cn.iocoder.yudao.module.data.dal.mysql.inputexec.InputExecMapper;

import static cn.iocoder.yudao.framework.common.exception.enums.GlobalErrorCodeConstants.INPUT_EXEC_NOT_EXISTS;
import static cn.iocoder.yudao.framework.common.exception.util.ServiceExceptionUtil.exception;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.convertList;
import static cn.iocoder.yudao.framework.common.util.collection.CollectionUtils.diffList;


/**
 * DSP数据导入 Service 实现类
 *
 * @author 芋道源码
 */
@Service
@Validated
public class InputExecServiceImpl implements InputExecService {

    @Resource
    private InputExecMapper inputExecMapper;

    @Override
    public Long createInputExec(InputExecSaveReqVO createReqVO) {
        // 插入
        InputExecDO inputExec = BeanUtils.toBean(createReqVO, InputExecDO.class);
        inputExecMapper.insert(inputExec);

        // 返回
        return inputExec.getId();
    }

    @Override
    public void updateInputExec(InputExecSaveReqVO updateReqVO) {
        // 校验存在
        validateInputExecExists(updateReqVO.getId());
        // 更新
        InputExecDO updateObj = BeanUtils.toBean(updateReqVO, InputExecDO.class);
        inputExecMapper.updateById(updateObj);
    }

    @Override
    public void deleteInputExec(Long id) {
        // 校验存在
        validateInputExecExists(id);
        // 删除
        inputExecMapper.deleteById(id);
    }

    @Override
        public void deleteInputExecListByIds(List<Long> ids) {
        // 删除
        inputExecMapper.deleteByIds(ids);
        }


    private void validateInputExecExists(Long id) {
        if (inputExecMapper.selectById(id) == null) {
            throw exception(INPUT_EXEC_NOT_EXISTS);
        }
    }

    @Override
    public InputExecDO getInputExec(Long id) {
        return inputExecMapper.selectById(id);
    }

    @Override
    public PageResult<InputExecDO> getInputExecPage(InputExecPageReqVO pageReqVO) {
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<InputExecDO> page = new com.baomidou.mybatisplus.extension.plugins.pagination.Page<>(pageReqVO.getPageNo(), pageReqVO.getPageSize());
        com.baomidou.mybatisplus.extension.plugins.pagination.Page<InputExecDO> resultPage = inputExecMapper.selectPage(page, pageReqVO);
        return new PageResult<>(resultPage.getRecords(), resultPage.getTotal());
    }

    @Override
    public List<InputExecDO> getDownExcelInput(Long id,String inputTime) {
        return inputExecMapper.getDownExcelInput(id, inputTime);
    }

}