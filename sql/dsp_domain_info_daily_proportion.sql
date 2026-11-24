-- 已有线上表只需添加统计日期列；proportion 列保存当天累计次数，已由线上维护。
ALTER TABLE `dsp_domain_info`
    ADD COLUMN `proportion_date` DATE NULL COMMENT '域名占比统计日期' AFTER `proportion`;
