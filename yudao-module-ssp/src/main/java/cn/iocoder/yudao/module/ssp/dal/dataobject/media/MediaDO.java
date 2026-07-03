package cn.iocoder.yudao.module.ssp.dal.dataobject.media;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体 DO
 *
 * @author 少年阿宾
 */
@TableName("ssp_media")
@KeySequence("ssp_media_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MediaDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 账号名
     */
    private String account;
    /**
     * 密码
     */
    private String password;
    /**
     * 公司简称
     */
    private String mediaCompanyShort;
    /**
     * 媒体名称
     */
    private String name;
    /**
     * 公司名称
     */
    private String mediaCompanyName;
    /**
     * 统一社会信用代码
     */
    private String mediaCompanyCode;
    /**
     * 营业执照照片
     */
    private String mediaCompanyLicense;
    /**
     * 公司地址
     */
    private String mediaCompanyAddress;
    /**
     * 法人姓名
     */
    private String mediaOwnerName;
    /**
     * 联系人
     */
    private String contactName;
    /**
     * 联系电话
     */
    private String contactPhone;
    /**
     * 联系邮箱
     */
    private String contactEmail;
    /**
     * 接入方式
     *
     * 枚举 {@link TODO ssp_access_type 对应的类}
     */
    private Integer accessType;
    /**
     * 流量类型
     *
     * 枚举 {@link TODO ssp_traffic_type 对应的类}
     */
    private Integer trafficType;
    /**
     * 媒体状态
     *
     * 枚举 {@link ssp_enable 对应的类}
     */
    private Integer enable;


}