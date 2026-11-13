package cn.iocoder.yudao.module.capturelog.controller.admin.capturelog;

import org.springframework.web.bind.annotation.*;
import javax.annotation.Resource;
import org.springframework.validation.annotation.Validated;
import org.springframework.security.access.prepost.PreAuthorize;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.Operation;

import java.util.*;

import cn.iocoder.yudao.framework.common.pojo.CommonResult;
import static cn.iocoder.yudao.framework.common.pojo.CommonResult.success;

import cn.iocoder.yudao.module.capturelog.controller.admin.capturelog.vo.CaptureLogVO;
import cn.iocoder.yudao.module.capturelog.mq.CaptureLogBuffer;

@Tag(name = "管理后台 - 捕获日志")
@RestController
@RequestMapping("/capture/log")
@Validated
public class CaptureLogController {

    @Resource
    private CaptureLogBuffer captureLogBuffer;

    @GetMapping("/list")
    @Operation(summary = "查询最近捕获日志", description = "内存缓冲内倒序返回（最新在前），列表只带报文预览，全文走 /get")
    public CommonResult<List<CaptureLogVO>> list(
            @Parameter(description = "捕获类型 req/res/ssp") @RequestParam(value = "type", required = false) String type,
            @Parameter(description = "DSP名称（模糊）") @RequestParam(value = "dspName", required = false) String dspName,
            @Parameter(description = "预算位ID") @RequestParam(value = "dspSlotId", required = false) Long dspSlotId,
            @Parameter(description = "流量位ID") @RequestParam(value = "sspSlotId", required = false) Long sspSlotId,
            @Parameter(description = "报文关键字（包含匹配）") @RequestParam(value = "keyword", required = false) String keyword,
            @Parameter(description = "返回条数上限，默认100") @RequestParam(value = "limit", required = false, defaultValue = "100") Integer limit) {
        return success(captureLogBuffer.list(type, dspName, dspSlotId, sspSlotId, keyword, limit));
    }

    @GetMapping("/get")
    @Operation(summary = "获得捕获日志完整报文", description = "按 seq 返回单条完整报文")
    @Parameter(name = "seq", description = "序号", required = true, example = "1024")
    public CommonResult<CaptureLogVO> get(@RequestParam("seq") Long seq) {
        return success(captureLogBuffer.get(seq));
    }

    @GetMapping("/stats")
    @Operation(summary = "获得缓冲状态", description = "当前条数/容量上限/被淘汰条数，判断缓冲是否够用")
    public CommonResult<Map<String, Object>> stats() {
        return success(captureLogBuffer.stats());
    }

}
