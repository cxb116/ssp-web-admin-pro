-- ----------------------------
-- yudao-module-ssp 域名模块建表脚本
-- 对应 DO: DomainCompanyDO / DomainDO / DomainInfoDO（均继承 BaseDO，且多租户已启用，必须包含 tenant_id）
-- 使用 CREATE TABLE IF NOT EXISTS，重复执行安全；已存在的表不会被修改
-- ----------------------------

-- 1. DSP域名公司列（本次服务器报错的缺失表）
CREATE TABLE IF NOT EXISTS `dsp_domain_company` (
    `id`            bigint       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `dsp_company_id` bigint      NULL DEFAULT NULL COMMENT '预算ID',
    `domain_name`   varchar(512) NULL DEFAULT NULL COMMENT '域名',
    `dsp_name`      varchar(255) NULL DEFAULT NULL COMMENT '所属公司',
    `remark`        varchar(500) NULL DEFAULT NULL COMMENT '备注',
    `creator`       varchar(64)  NULL DEFAULT '' COMMENT '创建者',
    `create_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`       varchar(64)  NULL DEFAULT '' COMMENT '更新者',
    `update_time`   datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `deleted`       bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`     bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_dsp_company_id` (`dsp_company_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'DSP域名公司列';

-- 2. DSP域名管理（同模块表，若服务器上也缺失则一并创建）
CREATE TABLE IF NOT EXISTS `dsp_domain` (
    `id`          bigint       NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `dsp_slot_id` bigint       NULL DEFAULT NULL COMMENT '预算ID',
    `domain_name` varchar(512) NULL DEFAULT NULL COMMENT '域名',
    `dsp_name`    varchar(255) NULL DEFAULT NULL COMMENT '所属公司',
    `creator`     varchar(64)  NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)  NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime     NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `deleted`     bit(1)       NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint       NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_dsp_slot_id` (`dsp_slot_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'DSP域名管理';

-- 3. DSP域名详情（同模块表，若服务器上也缺失则一并创建）
CREATE TABLE IF NOT EXISTS `dsp_domain_info` (
    `id`          bigint        NOT NULL AUTO_INCREMENT COMMENT 'ID',
    `dsp_slot_id` bigint        NULL DEFAULT NULL COMMENT '预算ID',
    `ssp_slot_id` bigint        NULL DEFAULT NULL COMMENT '媒体ID',
    `domains`     text          NULL COMMENT '域名集',
    `creator`     varchar(64)   NULL DEFAULT '' COMMENT '创建者',
    `create_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
    `updater`     varchar(64)   NULL DEFAULT '' COMMENT '更新者',
    `update_time` datetime      NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '最后更新时间',
    `deleted`     bit(1)        NOT NULL DEFAULT b'0' COMMENT '是否删除',
    `tenant_id`   bigint        NOT NULL DEFAULT 0 COMMENT '租户编号',
    PRIMARY KEY (`id`),
    KEY `idx_dsp_slot_id` (`dsp_slot_id`),
    KEY `idx_ssp_slot_id` (`ssp_slot_id`)
) ENGINE = InnoDB DEFAULT CHARSET = utf8mb4 COLLATE = utf8mb4_unicode_ci COMMENT = 'DSP域名详情';
