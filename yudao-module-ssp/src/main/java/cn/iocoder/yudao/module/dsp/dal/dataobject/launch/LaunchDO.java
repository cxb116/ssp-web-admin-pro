package cn.iocoder.yudao.module.dsp.dal.dataobject.launch;

import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.ssp.dal.dataobject.sspSlotInfo.SspSlotInfoDO;
import lombok.*;
import java.util.*;
import java.time.LocalDateTime;
import java.time.LocalDateTime;
import com.baomidou.mybatisplus.annotation.*;
import cn.iocoder.yudao.framework.mybatis.core.dataobject.BaseDO;

/**
 * 媒体预算绑定 DO
 *
 * @author 芋道源码
 */
@TableName("dsp_launch")
@KeySequence("dsp_launch_seq") // 用于 Oracle、PostgreSQL、Kingbase、DB2、H2 数据库的主键自增。如果是 MySQL 等数据库，可不写。
@Data
@EqualsAndHashCode(callSuper = true)
@ToString(callSuper = true)
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LaunchDO extends BaseDO {

    /**
     * ID
     */
    @TableId
    private Long id;
    /**
     * 流量广告位Id
     */
    private Long sspSlotId;
    /**
     * 预算广告位id
     */
    private Long dspSlotId;
    /**
     * 流量权重（最大值100）
     */
    private Integer trafficWeight;
    /**
     * 流量组，流量1，流量2
     */
    private Integer trafficGroup;
    /**
     * 底价 (给上游媒体底价,预算是rtb的时候就需要这个值)
     */
    private Long floorPrice;
    /**
     * 预算rtb时的分成系数
     */
    private Integer dspPayRatio;
    /**
     * 投放时段 1全时段 2 自定义
     */
    private Integer launchTime;
    /**
     * 投放时间段的时间字符串
     */
    private String launchHour;
    /**
     * 日志捕获时间戳
     */
    private Long logTime;
    /**
     * 请求次数
     */
    private Integer req;
    /**
     * 展现次数
     */
    private Integer ims;
    /**
     * 点击次数
     */
    private Integer clk;
    /**
     * 包透传 1 不透传 2透传
     */
    private Integer pkgTrans;

    /**
     * 预算集合
     */
    @TableField(exist = false)
    private List<DspSlotInfoDO> dspSlotInfoDO;


    /**
     * 媒体集合
     */
    @TableField(exist = false)
    private List<SspSlotInfoDO> sspSlotInfoDo;


}