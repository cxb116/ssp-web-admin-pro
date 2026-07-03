package cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 预算广告位 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_slot_info")
@KeySequence("dsp_slot_info_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DspSlotInfoDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 预算方广告位
     */
    private String dspSlotCode;
    /**
     * 产品ID
     */
    private Long productId;
    /**
     * 公司ID
     */
    private Long companyId;
    /**
     * 广告位名称
     */
    private String name;
    /**
     * 操作系统
     *
     * 枚举 {@link TODO ssp_os_type 对应的类}
     */
    private Integer osType;
    /**
     * 结算方式
     *
     * 枚举 {@link TODO ssp_pay_type 对应的类}
     */
    private Integer dspPayType;
    /**
     * 广告场景
     *
     * 枚举 {@link TODO ssp_ad_scene 对应的类}
     */
    private Long adScene;
    /**
     * 预算方APPKEY
     */
    private String dspAppKey;
    /**
     * 预算方APPSECRET
     */
    private String dspAppSecret;
    /**
     * 预算方APPID
     */
    private String dspAppId;
    /**
     * 预算方应用包名
     */
    private String dspAppPkg;
    /**
     * 应用版本号
     */
    private String dspAppVer;
    /**
     * 应用商店版本号
     */
    private String dspAppStoreVer;
    /**
     * 价格加密KEY
     */
    private String priceEncryptKey;
    /**
     * 应用商店地址
     */
    private String dspAppStoreLink;
    /**
     * 公司名称
     */
    @TableField(exist = false)
    private String companyName;
    /**
     * 预算名称
     */
    @TableField(exist = false)
    private String productName;


}