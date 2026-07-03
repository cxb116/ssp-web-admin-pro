package cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体广告位 DO
 *
 * @author 芋道源码
 */
@TableName("ssp_slot_info")
@KeySequence("ssp_slot_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SspSlotInfoDO extends BaseDO {

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
     * 应用id
     */
    private Long appId;
    /**
     * 广告位名称
     */
    private String name;
    /**
     * 内部广告位名称
     */
    private String nameAlise;
    /**
     * 广告场景
     *
     * 枚举 {@link TODO ssp_ad_scene 对应的类}
     */
    private Long adScene;
    /**
     * 样式尺寸
     *
     * 枚举 {@link TODO ssp_ad_size 对应的类}
     */
    private Long adSize;
    /**
     * 结算方式
     *
     * 枚举 {@link TODO ssp_pay_type 对应的类}
     */
    private Integer sspPayType;
    /**
     * 分成系数
     */
    private Integer sspDealRatio;
    /**
     * 固价
     */
    private Integer fixedPrice;
    /**
     * 广告位图片
     */
    private String adImage;
    /**
     * 广告位状态
     *
     * 枚举 {@link TODO ssp_enable 对应的类}
     */
    private Integer enable;

    /**
     * 媒体简称
     */
    @TableField(exist = false)
    private String mediaShortName;

    /**
     * 接入方式
     */
    @TableField(exist = false)
    private int accessType;

    /**
     *  app名称
     */
    @TableField(exist = false)
    private String appName;

    /**
     * 操作系统
     */
    @TableField(exist = false)
    private int osType;

    /**
     * ls 绑定条数
     */
    @TableField(exist = false)
    private int ls;

}