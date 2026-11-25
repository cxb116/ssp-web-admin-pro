package cn.iocoder.yudao.module.domain.dal.dataobject.domaininfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * DSP域名详情 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_domain_info")
@KeySequence("dsp_domain_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DomainInfoDO extends BaseDO {

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
     * 媒体ID
     */
    private Long sspSlotId;
    /**
     * 域名集
     */
    private String domains;

    /** 当日域名组上报次数，原始计数，不是百分比 */
    private Long proportion;

    /** proportion 对应的统计日期 */
    private LocalDate proportionDate;


}
