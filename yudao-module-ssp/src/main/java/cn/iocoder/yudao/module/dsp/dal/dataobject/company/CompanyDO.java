package cn.iocoder.yudao.module.dsp.dal.dataobject.company;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 预算广告 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_company")
@KeySequence("dsp_company_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CompanyDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 公司名称
     */
    private String name;
    /**
     * 预算映射值
     */
    private Long dspCode;
    /**
     * 请求地址
     */
    private String url;
    /**
     * 请求方法
     *
     * 枚举 {@link TODO ssp_request_type 对应的类}
     */
    private Integer method;
    /**
     * 超时时间
     */
    private Integer timeout;


}