-- 先运行本文件中的重复检查查询。
-- 若重复检查返回数据，先确认并处理重复组；下面的唯一键创建会因历史重复而失败，不会自动删除数据。

SELECT
    `ssp_slot_id`,
    `dsp_slot_id`,
    SHA2(COALESCE(`domains`, ''), 256) AS `domains_hash`,
    COUNT(*) AS `row_count`,
    GROUP_CONCAT(`id` ORDER BY `id`) AS `ids`
FROM `dsp_domain_info`
GROUP BY `ssp_slot_id`, `dsp_slot_id`, SHA2(COALESCE(`domains`, ''), 256)
HAVING COUNT(*) > 1;

-- 确认上面的重复检查无结果后，再执行以下 DDL。
ALTER TABLE `dsp_domain_info`
    ADD COLUMN `domains_hash` BINARY(32)
        GENERATED ALWAYS AS (UNHEX(SHA2(COALESCE(`domains`, ''), 256))) STORED,
    ADD UNIQUE KEY `uk_domain` (`ssp_slot_id`, `dsp_slot_id`, `domains_hash`);
