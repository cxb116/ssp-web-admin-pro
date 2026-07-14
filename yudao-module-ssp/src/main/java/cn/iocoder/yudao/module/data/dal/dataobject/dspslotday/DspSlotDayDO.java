package cn.iocoder.yudao.module.data.dal.dataobject.dspslotday;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * DSP预算广告位日期报 DO
 *
 * @author 芋道源码
 */
@TableName("data_dsp_slot_day")
@KeySequence("data_dsp_slot_day_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DspSlotDayDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 预算位ID
     */
    private Long dspSlotId;
    /**
     * 预算广告位ID
     */
    private String dspSlotCode;
    /**
     * 媒体广告ID
     */
    private Long sspSlotId;
    /**
     * 展示PV
     */
    private Long showPv;
    /**
     * 展示UV
     */
    private Long showUv;
    /**
     * 点击PV
     */
    private Long clickPv;
    /**
     * 点击UV
     */
    private Long clickUv;
    /**
     * 请求PV
     */
    private Long reqPv;
    /**
     * 请求UV
     */
    private Long reqUv;
    /**
     * 丢弃请求
     */
    private Long discard;
    /**
     * 返回PV
     */
    private Long retPv;
    /**
     * 返回UV
     */
    private Long retUv;
    /**
     * 成本(分)
     */
    private Long spend;
    /**
     * 收入(分)
     */
    private Long income;
    /**
     * 折后点击
     */
    private Long discountClickPv;
    /**
     * 折后展示
     */
    private Long discountShowPv;
    /**
     * 调起成功
     */
    private Long dplsuccPv;
    /**
     * 完成量
     */
    private Long completePv;
    /**
     * 安装量
     */
    private Long installPv;
    /**
     * 激活量
     */
    private Long activatePv;
    /**
     * 时间(yyyyMMdd / yyyyMMddHH)
     */
    private Integer date;
    /**
     * 创建时间戳
     */
    private Integer createdAt;

    /**
     * 公司名称（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private String companyName;

    /**
     * 产品名称（非表字段，关联查询获取）
     */
    @TableField(exist = false)
    private String productName;

}