package cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo;

import cn.iocoder.yudao.module.dsp.dal.dataobject.launch.LaunchDO;
import cn.iocoder.yudao.module.dsp.dal.dataobject.company.CompanyDO;
import cn.iocoder.yudao.module.dsp.dal.mysql.company.CompanyMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.dspslotinfo.DspSlotInfoMapper;
import cn.iocoder.yudao.module.dsp.dal.mysql.launch.LaunchMapper;
import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import javax.validation.constraints.*;
import javax.validation.*;
import javax.servlet.http.*;
import java.util.*;
import java.io.IOException;

import cn.iocoder.yudao.framework.common.pojo.PageParam;
import cn.iocoder.yudao.framework.common.pojo.PageResult;
import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import cn.iocoder.yudao.framework.common.util.object.BeanUtils;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.framework.excel.core.util.ExcelUtils;

import cn.iocoder.yudao.framework.apilog.core.annotation.ApiAccessLog;
import static cn.iocoder.yudao.framework.apilog.core.enums.OperateTypeEnum.*;

import cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo.*;
import cn.iocoder.yudao.module.dsp.dal.dataobject.dspslotinfo.DspSlotInfoDO;
import cn.iocoder.yudao.module.dsp.service.dspslotinfo.DspSlotInfoService;

@Tag(name = "管理后台 - 预算广告位")
@RestController
@RequestMapping("/dsp/slot-info")
@Validated
public class DspSlotInfoController {

    @Resource
    private DspSlotInfoService slotInfoService;
    @Resource
    private LaunchMapper launchMapper;
    @Resource
    private CompanyMapper companyMapper;

    /**
     * 回填公司名称：DO 只有 companyId，RespVO.companyName 需按公司 ID 批量关联 dsp/company 查询
     */
    private void fillCompanyName(List<DspSlotInfoRespVO> list) {
        if (list == null || list.isEmpty()) {
            return;
        }
        Set<Long> companyIds = new HashSet<>();
        for (DspSlotInfoRespVO vo : list) {
            if (vo.getCompanyId() != null) {
                companyIds.add(vo.getCompanyId());
            }
        }
        if (companyIds.isEmpty()) {
            return;
        }
        Map<Long, String> nameMap = new HashMap<>();
        for (CompanyDO company : companyMapper.selectBatchIds(companyIds)) {
            nameMap.put(company.getId(), company.getName());
        }
        for (DspSlotInfoRespVO vo : list) {
            vo.setCompanyName(nameMap.get(vo.getCompanyId()));
        }
    }


    @PostMapping("/create")
    @Operation(summary = "创建预算广告位")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:create')")
    public CommonResult<Long> createSlotInfo(@Valid @RequestBody DspSlotInfoSaveReqVO createReqVO) {
        return success(slotInfoService.createSlotInfo(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新预算广告位")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:update')")
    public CommonResult<Boolean> updateSlotInfo(@Valid @RequestBody DspSlotInfoSaveReqVO updateReqVO) {
        slotInfoService.updateSlotInfo(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除预算广告位")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfo(@RequestParam("id") Long id) {
        slotInfoService.deleteSlotInfo(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除预算广告位")
                @PreAuthorize("@ss.hasPermission('dsp:slot-info:delete')")
    public CommonResult<Boolean> deleteSlotInfoList(@RequestParam("ids") List<Long> ids) {
        slotInfoService.deleteSlotInfoListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得预算广告位")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<DspSlotInfoRespVO> getSlotInfo(@RequestParam("id") Long id) {
        DspSlotInfoDO slotInfo = slotInfoService.getSlotInfo(id);
        return success(BeanUtils.toBean(slotInfo, DspSlotInfoRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得预算广告位分页")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<PageResult<DspSlotInfoRespVO>> getSlotInfoPage(@Valid DspSlotInfoPageReqVO pageReqVO) {
        PageResult<DspSlotInfoDO> pageResult = slotInfoService.getSlotInfoPage(pageReqVO);
        PageResult<DspSlotInfoRespVO> respPage = BeanUtils.toBean(pageResult, DspSlotInfoRespVO.class);
        fillCompanyName(respPage.getList());
        return success(respPage);
    }

    // 媒体绑定预算的界面
    @GetMapping("/page-info")
    @Operation(summary = "获得预算广告位分页")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:query')")
    public CommonResult<PageResult<DspSlotInfoRespVO>> getSlotInfoPageInfo(@Valid DspSlotInfoPageReqVO pageReqVO) {
        PageResult<DspSlotInfoDO> pageResult = slotInfoService.getSlotInfoPage(pageReqVO);
        pageResult.getList().forEach(slotInfoDO -> {
            List<LaunchDO> launchDOS = launchMapper.selectLaunchBySspSlotIdDspSlotId(pageReqVO.getSspSlotId(),slotInfoDO.getId());
            if (launchDOS != null && launchDOS.size() > 0) {
                slotInfoDO.setSspTotal(launchDOS.size());
            }
        });

        PageResult<DspSlotInfoRespVO> respPage = BeanUtils.toBean(pageResult, DspSlotInfoRespVO.class);
        fillCompanyName(respPage.getList());
        return success(respPage);
    }


    @GetMapping("/export-excel")
    @Operation(summary = "导出预算广告位 Excel")
    @PreAuthorize("@ss.hasPermission('dsp:slot-info:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportSlotInfoExcel(@Valid DspSlotInfoPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(1000);
        List<DspSlotInfoDO> list = slotInfoService.getSlotInfoPage(pageReqVO).getList();
        List<DspSlotInfoRespVO> respList = BeanUtils.toBean(list, DspSlotInfoRespVO.class);
        fillCompanyName(respList);
        // 导出 Excel
        ExcelUtils.write(response, "预算广告位.xls", "数据", DspSlotInfoRespVO.class, respList);
    }

}