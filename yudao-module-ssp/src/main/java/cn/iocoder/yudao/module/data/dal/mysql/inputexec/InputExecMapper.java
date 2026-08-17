package cn.iocoder.yudao.module.data.dal.mysql.inputexec;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.data.dal.dataobject.inputexec.InputExecDO;
import cn.iocoder.yudao.module.data.controller.admin.inputexec.vo.*;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * DSP数据导入 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface InputExecMapper extends BaseMapperX<InputExecDO> {

    /**
     * 分页查询
     */
    Page<InputExecDO> selectPage(Page<InputExecDO> page, @Param("reqVO") InputExecPageReqVO reqVO);

    /**
     * 导入数据
     * @param id
     * @return
     */
    List<InputExecDO> getDownExcelInput(@Param("id") Long id, @Param("inputTime") String inputTime);

    void updateInputExec(InputExecDO inputExecDO);
}