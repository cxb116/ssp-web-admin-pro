package cn.iocoder.yudao.framework.common.exception.enums;

import cn.iocoder.yudao.framework.common.exception.ErrorCode;

/**
 * 全局错误码枚举
 * 0-999 系统异常编码保留
 *
 * 一般情况下，使用 HTTP 响应状态码 https://developer.mozilla.org/zh-CN/docs/Web/HTTP/Status
 * 虽然说，HTTP 响应状态码作为业务使用表达能力偏弱，但是使用在系统层面还是非常不错的
 * 比较特殊的是，因为之前一直使用 0 作为成功，就不使用 200 啦。
 *
 * @author 芋道源码
 */
public interface GlobalErrorCodeConstants {

    ErrorCode SUCCESS = new ErrorCode(0, "成功");

    // ========== 客户端错误段 ==========

    ErrorCode BAD_REQUEST = new ErrorCode(400, "请求参数不正确");
    ErrorCode UNAUTHORIZED = new ErrorCode(401, "账号未登录");
    ErrorCode FORBIDDEN = new ErrorCode(403, "没有该操作权限");
    ErrorCode NOT_FOUND = new ErrorCode(404, "请求未找到");
    ErrorCode METHOD_NOT_ALLOWED = new ErrorCode(405, "请求方法不正确");
    ErrorCode LOCKED = new ErrorCode(423, "请求失败，请稍后重试"); // 并发请求，不允许
    ErrorCode TOO_MANY_REQUESTS = new ErrorCode(429, "请求过于频繁，请稍后重试");

    // ========== 服务端错误段 ==========

    ErrorCode INTERNAL_SERVER_ERROR = new ErrorCode(500, "系统异常");
    ErrorCode NOT_IMPLEMENTED = new ErrorCode(501, "功能未实现/未开启");
    ErrorCode ERROR_CONFIGURATION = new ErrorCode(502, "错误的配置项");

    // ========== 自定义错误段 ==========
    ErrorCode REPEATED_REQUESTS = new ErrorCode(900, "重复请求，请稍后重试"); // 重复请求
    ErrorCode DEMO_DENY = new ErrorCode(901, "演示模式，禁止写操作");

    ErrorCode UNKNOWN = new ErrorCode(999, "未知错误");


    // ========== 媒体 1300 ==========
    ErrorCode MEDIA_NOT_EXISTS = new ErrorCode(1300, "媒体不存在");
    ErrorCode MEDIA_HAS_APP = new ErrorCode(1301, "该媒体下存在应用，禁止删除");

    // ========== 媒体应用 1400 ==========
    ErrorCode APP_NOT_EXISTS = new ErrorCode(1400, "媒体应用不存在");
    ErrorCode APP_HAS_SSP_SLOT = new ErrorCode(1401, "该应用已绑定媒体广告位，禁止删除");


    // ========== 媒体广告位 1500 ==========
    ErrorCode SLOT_INFO_NOT_EXISTS = new ErrorCode(1500, "媒体广告位不存在");
    ErrorCode SSP_SLOT_HAS_LAUNCH = new ErrorCode(1501, "该媒体广告位已被投放关系引用，禁止删除");

    // ========== 媒体预算绑定1600 ==========
    ErrorCode LAUNCH_NOT_EXISTS = new ErrorCode(1600, "媒体预算绑定不存在");

    // ========== 预算广告 1700 ==========
    ErrorCode COMPANY_NOT_EXISTS = new ErrorCode(1700, "预算广告不存在");
    ErrorCode COMPANY_DSP_CODE_EXISTS = new ErrorCode(1701, "预算映射值已存在");


    // ========== 预算产品广告 1700 ==========
    ErrorCode PRODUCT_NOT_EXISTS = new ErrorCode(1800, "预算广告不存在");
    ErrorCode PRODUCT_HAS_COMPANY = new ErrorCode(1801, "该预算产品已绑定预算公司，禁止删除");
    ErrorCode PRODUCT_HAS_DSP_SLOT = new ErrorCode(1802, "该预算产品已被预算广告位引用，禁止删除");
    ErrorCode PRODUCT_NAME_EXISTS = new ErrorCode(1803, "该公司下已存在同名的产品名称");

    // ========== 预算广告位 1800 ==========
    ErrorCode DSP_SLOT_INFO_NOT_EXISTS = new ErrorCode(1900, "预算广告位不存在");
    ErrorCode DSP_SLOT_HAS_LAUNCH = new ErrorCode(1901, "该预算广告位已被投放关系引用，禁止删除");

    //=============  DSP预算广告位日期报 1900 ==========
    ErrorCode DSP_SLOT_DAY_NOT_EXISTS = new ErrorCode(1900, "DSP预算广告位日期报不存在");

    // ========== DSP预算广告位小时报 2000 ==========
    ErrorCode DSP_SLOT_HOUR_NOT_EXISTS = new ErrorCode(2000, "DSP预算广告位小时报不存在");


    // ========== DSP-SSP广告位报 2100 ==========
    ErrorCode SSP_SLOT_DAY_NOT_EXISTS = new ErrorCode(2100, "DSP-SSP广告位报不存在");

    // ========== DSP-SSP广告位报 2200 ==========
    ErrorCode SSP_SLOT_HOUR_NOT_EXISTS = new ErrorCode(2200, "DSP-SSP广告位报不存在");

    // ========== DSP数据导入 2300 ==========
    ErrorCode INPUT_EXEC_NOT_EXISTS = new ErrorCode(2300, "DSP数据导入不存在");
}
