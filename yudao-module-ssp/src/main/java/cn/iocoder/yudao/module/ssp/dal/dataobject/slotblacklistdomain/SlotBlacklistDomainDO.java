package cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklistdomain;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 黑名单统计 DO
 *
 * @author 芋道源码
 */
@TableName("ssp_slot_blacklist_domain")
@KeySequence("ssp_slot_blacklist_domain_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotBlacklistDomainDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 媒体id
     */
    private Long mediaId;
    /**
     * 拦截域名
     */
    private String domain;


}