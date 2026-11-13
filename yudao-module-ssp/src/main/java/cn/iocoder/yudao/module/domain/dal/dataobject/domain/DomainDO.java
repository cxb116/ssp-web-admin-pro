package cn.iocoder.yudao.module.domain.dal.dataobject.domain;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * DSP域名管理 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_domain")
@KeySequence("dsp_domain_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 预算ID
     */
    private Long dspSlotId;
    /**
     *  域名
     */
    private String domainName;
    /**
     * 所属公司
     */
    private String dspName;


}