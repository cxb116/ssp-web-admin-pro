package cn.iocoder.yudao.module.ssp.dal.dataobject.slotblacklist;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 黑名单配置 DO
 *
 * @author 芋道源码
 */
@TableName("ssp_slot_blacklist")
@KeySequence("ssp_slot_blacklist_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SlotBlacklistDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 媒体ID
     */
    private Long mediaId;
    /**
     * 域名列表
     */
    private String backlistDomains;
    /**
     * 备注
     */
    private String remark;
    /**
     * 是否开启： 1开启，2关闭
     */
    private String status;


}