package cn.iocoder.yudao.module.data.service.inputexec;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP数据导入 Service 接口
 *
 * @author 芋道源码
 */
public interface InputExecService {

    /**
     * 创建DSP数据导入
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInputExec(@Valid InputExecSaveReqVO createReqVO);

    /**
     * 更新DSP数据导入
     *
     * @param updateReqVO 更新信息
     */
    void updateInputExec(@Valid InputExecSaveReqVO updateReqVO);

    /**
     * 删除DSP数据导入
     *
     * @param id 编号
     */
    void deleteInputExec(Long id);

    /**
    * 批量删除DSP数据导入
    *
    * @param ids 编号
    */
    void deleteInputExecListByIds(List<Long> ids);

    /**
     * 获得DSP数据导入
     *
     * @param id 编号
     * @return DSP数据导入
     */
    InputExecDO getInputExec(Long id);

    /**
     * 获得DSP数据导入分页
     *
     * @param pageReqVO 分页查询
     * @return DSP数据导入分页
     */
    PageResult<InputExecDO> getInputExecPage(InputExecPageReqVO pageReqVO);

    List<InputExecDO> getDownExcelInput(Long id,String inputTime);

}