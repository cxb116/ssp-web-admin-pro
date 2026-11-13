package cn.iocoder.yudao.module.domain.service.domaininfo;

import java.util.*;
import javax.validation.*;
import cn.iocoder.yudao.module.domain.controller.admin.domaininfo.vo.*;
import cn.iocoder.yudao.module.domain.dal.dataobject.domaininfo.DomainInfoDO;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.PageParam;

/**
 * DSP域名详情 Service 接口
 *
 * @author 芋道源码
 */
public interface DomainInfoService {

    /**
     * 创建DSP域名详情
     *
     * @param createReqVO 创建信息
     * @return 编号
     */
    Long createInfo(@Valid DomainInfoSaveReqVO createReqVO);

    /**
     * 更新DSP域名详情
     *
     * @param updateReqVO 更新信息
     */
    void updateInfo(@Valid DomainInfoSaveReqVO updateReqVO);

    /**
     * 删除DSP域名详情
     *
     * @param id 编号
     */
    void deleteInfo(Long id);

    /**
    * 批量删除DSP域名详情
    *
    * @param ids 编号
    */
    void deleteInfoListByIds(List<Long> ids);

    /**
     * 获得DSP域名详情
     *
     * @param id 编号
     * @return DSP域名详情
     */
    DomainInfoDO getInfo(Long id);

    /**
     * 获得DSP域名详情分页（按 dsp_slot_id 分组合并：domains 去重重组，一个 dsp_slot_id 只返回一条）
     *
     * @param pageReqVO 分页查询
     * @return DSP域名详情分页
     */
    PageResult<DomainInfoRespVO> getInfoPage(DomainInfoPageReqVO pageReqVO);

    /**
     * 获得指定预算位的全部原始记录（不分组合并，一条记录即一组上报域名）
     *
     * @param dspSlotId 预算位 ID
     * @return 记录列表
     */
    List<DomainInfoRespVO> getInfoListByDspSlotId(Long dspSlotId, Integer limit);

}