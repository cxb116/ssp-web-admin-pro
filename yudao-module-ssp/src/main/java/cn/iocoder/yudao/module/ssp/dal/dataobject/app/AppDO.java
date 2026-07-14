package cn.iocoder.yudao.module.ssp.dal.dataobject.app;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体应用 DO
 *
 * @author 芋道源码
 */
@TableName("ssp_app")
@KeySequence("ssp_app_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AppDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 媒体Id
     */
    private Long mediaId;
    /**
     * 媒体Id
     */
    @TableField(exist = false)
    private String mediaShort;
    /**
     * 应用名称
     */
    private String name;
    /**
     * 操作系统
     *
     * 枚举
     */
    private Integer osType;
    /**
     * 接入方式
     *
     * 枚举
     */
    private Integer accessType;
    /**
     * 包名
     */
    private String pkg;
    /**
     * 下载地址
     */
    private String downloadUrl;
    /**
     * 应用状态
     *
     * 枚举
     */
    private Integer enable;


}
