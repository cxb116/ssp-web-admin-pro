-- JSON转化失败 告警站内信模板（CaptureJsonFailNotifier 每分钟聚合推送用）
-- 模板 code 必须为 capture_json_fail（与代码常量一致）；params 为模板占位参数列表
-- type: 1=公告 2=系统消息；status: 0=开启
INSERT INTO system_notify_template
    (name, code, nickname, content, type, params, status, remark, creator, updater, deleted, tenant_id)
VALUES (
    'JSON转化失败告警',
    'capture_json_fail',
    '系统监控',
    '【{time}】近 1 分钟 DSP 响应 JSON 转化失败 {count} 次：预算位ID {dspSlotId}，流量位ID {sspSlotId}，请检查该预算对接',
    2,
    '["count","dspSlotId","sspSlotId","time"]',
    0,
    'dsp-api 捕获日志 JSON转化失败 事件每分钟聚合告警（CaptureJsonFailNotifier）',
    '1', '1', b'0', 1
);
