package cn.iocoder.yudao.module.ssp.controller.admin.media;

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

import cn.iocoder.yudao.module.ssp.controller.admin.media.vo.*;
import cn.iocoder.yudao.module.ssp.dal.dataobject.media.MediaDO;
import cn.iocoder.yudao.module.ssp.service.media.MediaService;

@Tag(name = "管理后台 - 媒体")
@RestController
@RequestMapping("/ssp/media")
@Validated
public class MediaController {

    @Resource
    private MediaService mediaService;

    @PostMapping("/create")
    @Operation(summary = "创建媒体")
    @PreAuthorize("@ss.hasPermission('ssp:media:create')")
    public CommonResult<Long> createMedia(@Valid @RequestBody MediaSaveReqVO createReqVO) {
        return success(mediaService.createMedia(createReqVO));
    }

    @PutMapping("/update")
    @Operation(summary = "更新媒体")
    @PreAuthorize("@ss.hasPermission('ssp:media:update')")
    public CommonResult<Boolean> updateMedia(@Valid @RequestBody MediaSaveReqVO updateReqVO) {
        mediaService.updateMedia(updateReqVO);
        return success(true);
    }

    @DeleteMapping("/delete")
    @Operation(summary = "删除媒体")
    @Parameter(name = "id", description = "编号", required = true)
    @PreAuthorize("@ss.hasPermission('ssp:media:delete')")
    public CommonResult<Boolean> deleteMedia(@RequestParam("id") Long id) {
        mediaService.deleteMedia(id);
        return success(true);
    }

    @DeleteMapping("/delete-list")
    @Parameter(name = "ids", description = "编号", required = true)
    @Operation(summary = "批量删除媒体")
                @PreAuthorize("@ss.hasPermission('ssp:media:delete')")
    public CommonResult<Boolean> deleteMediaList(@RequestParam("ids") List<Long> ids) {
        mediaService.deleteMediaListByIds(ids);
        return success(true);
    }

    @GetMapping("/get")
    @Operation(summary = "获得媒体")
    @Parameter(name = "id", description = "编号", required = true, example = "1024")
    @PreAuthorize("@ss.hasPermission('ssp:media:query')")
    public CommonResult<MediaRespVO> getMedia(@RequestParam("id") Long id) {
        MediaDO media = mediaService.getMedia(id);
        return success(BeanUtils.toBean(media, MediaRespVO.class));
    }

    @GetMapping("/page")
    @Operation(summary = "获得媒体分页")
    @PreAuthorize("@ss.hasPermission('ssp:media:query')")
    public CommonResult<PageResult<MediaRespVO>> getMediaPage(@Valid MediaPageReqVO pageReqVO) {
        PageResult<MediaDO> pageResult = mediaService.getMediaPage(pageReqVO);
        return success(BeanUtils.toBean(pageResult, MediaRespVO.class));
    }

    @GetMapping("/export-excel")
    @Operation(summary = "导出媒体 Excel")
    @PreAuthorize("@ss.hasPermission('ssp:media:export')")
    @ApiAccessLog(operateType = EXPORT)
    public void exportMediaExcel(@Valid MediaPageReqVO pageReqVO,
              HttpServletResponse response) throws IOException {
        pageReqVO.setPageSize(PageParam.PAGE_SIZE_NONE);
        List<MediaDO> list = mediaService.getMediaPage(pageReqVO).getList();
        // 导出 Excel
        ExcelUtils.write(response, "媒体.xls", "数据", MediaRespVO.class,
                        BeanUtils.toBean(list, MediaRespVO.class));
    }

}