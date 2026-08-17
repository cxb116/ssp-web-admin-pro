package cn.iocoder.yudao.module.data.dal.dataobject.inputexec;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.util.*;

import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;
import org.springframework.format.annotation.DateTimeFormat;

/**
 * DSP数据导入 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_input_exec")
@KeySequence("dsp_input_exec_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InputExecDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 预算公司id
     */
    private Long companyId;
    /**
     * 导入条数
     */
    private Long tables;
    /**
     * 导入时间
     */
    private String inputTime;

    /**
     * 公司名称（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 广告位ID（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private Long sspSlotId;

    /**
     * 预算方广告位ID（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private String dspSlotCode;

    /**
     * 媒体公司（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private String mediaCompany;
}