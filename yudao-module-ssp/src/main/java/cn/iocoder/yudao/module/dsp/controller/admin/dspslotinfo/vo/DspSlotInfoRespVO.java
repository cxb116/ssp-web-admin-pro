package cn.iocoder.yudao.module.dsp.controller.admin.dspslotinfo.vo;

import com.baomidou.mybatisplus.annotation.TableField;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import java.util.*;
import org.springframework.format.annotation.DateTimeFormat;
import java.time.LocalDateTime;
import cn.idev.excel.annotation.*;
import cn.iocoder.yudao.framework.excel.core.annotations.DictFormat;
import cn.iocoder.yudao.framework.excel.core.convert.DictConvert;

@Schema(description = "管理后台 - 预算广告位 Response VO")
@Data
@ExcelIgnoreUnannotated
public class DspSlotInfoRespVO {

    @Schema(description = "ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "32243")
    @ExcelProperty("预算位ID")
    private Long id;

    @Schema(description = "广告位名称", requiredMode = Schema.RequiredMode.REQUIRED, example = "赵六")
    @ExcelProperty("预算名称")
    private String name;

    @Schema(description = "预算广告位", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("预算广告位ID")
    private String dspSlotCode;

    @Schema(description = "产品ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "31959")
//    @ExcelProperty("产品ID")
    private Long productId;

    @Schema(description = "公司ID", requiredMode = Schema.RequiredMode.REQUIRED, example = "16821")
//    @ExcelProperty("公司ID")
    private Long companyId;



    @Schema(description = "产品名称")
    @ExcelProperty("产品名称")
    private String productName;

    @Schema(description = "公司名称")
    @ExcelProperty("公司名称")
    private String companyName;

    @Schema(description = "操作系统", requiredMode = Schema.RequiredMode.REQUIRED, example = "2")
    @ExcelProperty(value = "操作系统", converter = DictConvert.class)
    @DictFormat("ssp_os_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer osType;

    @Schema(description = "结算方式", requiredMode = Schema.RequiredMode.REQUIRED, example = "1")
    @ExcelProperty(value = "结算方式", converter = DictConvert.class)
    @DictFormat("ssp_pay_type") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Integer dspPayType;

    @Schema(description = "广告场景", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty(value = "广告场景", converter = DictConvert.class)
    @DictFormat("ssp_ad_scene") // TODO 代码优化：建议设置到对应的 DictTypeConstants 枚举类中
    private Long adScene;

    @Schema(description = "预算方APPKEY")
    @ExcelProperty("预算方APPKEY")
    private String dspAppKey;

    @Schema(description = "预算方APPSECRET")
    @ExcelProperty("预算方APPSECRET")
    private String dspAppSecret;

    @Schema(description = "预算方APPID", example = "30241")
    @ExcelProperty("预算方APPID")
    private String dspAppId;

    @Schema(description = "预算方应用包名")
    @ExcelProperty("应用包名")
    private String dspAppPkg;

    @Schema(description = "应用版本号")
    @ExcelProperty("应用版本号")
    private String dspAppVer;

    @Schema(description = "应用商店版本号")
    @ExcelProperty("应用商店版本号")
    private String dspAppStoreVer;

    @Schema(description = "价格加密KEY")
    @ExcelProperty("价格加密KEY")
    private String priceEncryptKey;

    @Schema(description = "应用商店地址")
    @ExcelProperty("应用商店地址")
    private String dspAppStoreLink;



    @Schema(description = "创建者")
    @ExcelProperty("创建者")
    private String creator;

    @Schema(description = "创建时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("创建时间")
    private LocalDateTime createTime;

    @Schema(description = "更新者")
    @ExcelProperty("更新者")
    private String updater;

    @Schema(description = "更新时间", requiredMode = Schema.RequiredMode.REQUIRED)
    @ExcelProperty("更新时间")
    private LocalDateTime updateTime;

    @Schema(description = "媒体绑定数量")
    @TableField(exist = false)
    private Integer ls;

    @Schema(description = "媒体绑定数量")
    @TableField(exist = false)
    private Integer sspTotal;

}