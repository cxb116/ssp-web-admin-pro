package cn.iocoder.yudao.module.domain.dal.mysql.domaininfo;

import java.util.*;
import java.time.LocalDate;

import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.mybatis.core.query.LambdaQueryWrapperX;
import cn.iocoder.yudao.framework.mybatis.core.mapper.BaseMapperX;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaininfo.DomainInfoDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import com.baomidou.mybatisplus.annotation.InterceptorIgnore;
import cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo.*;

/**
 * DSP域名详情 Mapper
 *
 * @author 芋道源码
 */
@Mapper
public interface DomainInfoMapper extends BaseMapperX<DomainInfoDO> {

    /** 查询指定预算位、统计日下全部域名组次数总和（不受详情列表条数限制）。 */
    @Select("SELECT COALESCE(SUM(proportion), 0) AS totalCount " +
            "FROM dsp_domain_info WHERE dsp_slot_id = #{dspSlotId} " +
            "AND proportion_date = #{statDate} AND deleted = 0")
    Long selectDailyGroupCountTotal(@Param("dspSlotId") Long dspSlotId,
                                    @Param("statDate") java.time.LocalDate statDate);

    default PageResult<DomainInfoDO> selectPage(DomainInfoPageReqVO reqVO) {
        return selectPage(reqVO, new LambdaQueryWrapperX<DomainInfoDO>()
                .eqIfPresent(DomainInfoDO::getDspSlotId, reqVO.getDspSlotId())
                .eqIfPresent(DomainInfoDO::getSspSlotId, reqVO.getSspSlotId())
                .eqIfPresent(DomainInfoDO::getDomains, reqVO.getDomains())
                .betweenIfPresent(DomainInfoDO::getCreateTime, reqVO.getCreateTime())
                .orderByDesc(DomainInfoDO::getId));
    }

    /**
     * 查询指定预算位的域名组：当日数据按上报次数（即占比）倒序，其余日期靠后。
     * 【性能】永久留底后活跃预算位累计几千条，无 limit 全量返回曾把详情页拖死。
     *
     * @param dspSlotId 预算位 ID
     * @param limit     返回条数上限，null 用默认 100；clamp 到 [1,500] 后拼接（数字白名单，无注入风险）
     */
    default List<DomainInfoDO> selectListByDspSlotId(Long dspSlotId, Integer limit) {
        int n = limit == null ? 100 : limit;
        n = Math.min(Math.max(n, 1), 500);
        String today = LocalDate.now().toString();
        return selectList(new LambdaQueryWrapperX<DomainInfoDO>()
                .eq(DomainInfoDO::getDspSlotId, dspSlotId)
                .last("ORDER BY CASE WHEN proportion_date = '" + today + "' THEN 0 ELSE 1 END ASC, " +
                        "CASE WHEN proportion_date = '" + today + "' THEN proportion ELSE 0 END DESC, id DESC LIMIT " + n));
    }

    /**
     * 按 dsp_slot_id 分组统计组数（一个 dsp_slot_id 只算一条）
     *
     * @param reqVO 过滤条件
     * @return 组数
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（COUNT DISTINCT）会导致多租户拦截器的 JSQLParser 解析失败，此处跳过租户 SQL 解析
    Long selectDspSlotGroupCount(@Param("reqVO") DomainInfoPageReqVO reqVO);

    /**
     * 按 dsp_slot_id 分组分页，返回本页的 dsp_slot_id 列表。
     * 默认按组内最大 id 倒序；orderByReqPv 非 null 时按当天请求PV汇总排序（SQL 内 JOIN data_dsp_slot_day 子查询）
     *
     * @param reqVO        过滤条件
     * @param offset       偏移量
     * @param limit        每页条数；null 表示不分页（Excel 导出用）
     * @param dateBegin    当天开始日期（含），如 20261106，用于请求PV排序子查询
     * @param dateEnd      当天结束日期（不含），如 20261107
     * @param orderByReqPv 请求PV排序方向："ASC"/"DESC"；null 表示不按请求PV排序
     * @return 本页的 dsp_slot_id 列表
     */
    @InterceptorIgnore(tenantLine = "true") // 聚合查询（GROUP BY）会导致多租户拦截器的 JSQLParser 解析失败，此处跳过租户 SQL 解析
    List<Long> selectDspSlotGroupPage(@Param("reqVO") DomainInfoPageReqVO reqVO,
                                      @Param("offset") int offset,
                                      @Param("limit") Integer limit,
                                      @Param("dateBegin") long dateBegin,
                                      @Param("dateEnd") long dateEnd,
                                      @Param("orderByReqPv") String orderByReqPv);

    /**
     * 查询匹配过滤条件的分组的全部域名明细，用于按域名总数、单组域名最大数排序。
     *
     * @param reqVO 过滤条件
     * @return 分组内全部记录（仅 id、dsp_slot_id、domains）
     */
    @InterceptorIgnore(tenantLine = "true") // 子查询聚合会导致多租户拦截器的 JSQLParser 解析失败，此处跳过租户 SQL 解析
    List<DomainInfoDO> selectDspSlotGroupDomainRows(@Param("reqVO") DomainInfoPageReqVO reqVO);

}
