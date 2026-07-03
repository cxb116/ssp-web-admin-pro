package cn.iocoder.yudao.module.data.dal.dataobject.sspslothour;

import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * DSP-SSP广告位报 DO
 *
 * @author 芋道源码
 */
@TableName("data_ssp_slot_hour")
@KeySequence("data_ssp_slot_hour_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SspSlotHourDO extends BaseDO {

    /**
     * 主键
     */
    @TableId
    private Long id;
    /**
     * 媒体用户Id
     */
    private Long mediaId;
    /**
     * 应用ID
     */
    private Long appId;
    /**
     * SSP广告位ID
     */
    private Long sspSlotId;
    /**
     * DSP广告位ID
     */
    private Long dspSlotId;
    /**
     * 预算广告位编号
     */
    private String dspSlotCode;
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
     * 请求数
     */
    private Long reqCount;
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
     * 时间 yyyyMMddHH
     */
    private Long date;
    /**
     * 创建时间戳
     */
    private Long createdAt;


}