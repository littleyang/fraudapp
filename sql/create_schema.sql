CREATE TABLE `blacklist` (
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '黑名单id',
`account` varchar(64) NOT NULL COMMENT '黑名单对应的账户',
`name` varchar(64) NOT NULL COMMENT '黑名单对应的name',
`reason` varchar(2000) NOT NULL COMMENT '加入黑名单的原因',
`status` int(4) NOT NULL DEFAULT '0' COMMENT '状态描述',
`gmt_creator` varchar(30) NOT NULL DEFAULT 't' COMMENT '创建人',
`gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`gmt_updater` varchar(30) NOT NULL DEFAULT 't' COMMENT '更新人',
PRIMARY KEY (`id`),
UNIQUE KEY `uq_account` (`account`),
KEY `account` (`account`) USING BTREE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='黑名单记录表';


CREATE TABLE `rule` (
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '规则id',
`rule_key` varchar(64) NOT NULL COMMENT 'id对应的key，如amount_threshold,',
`rule_type` varchar(64) NOT NULL COMMENT '检查规则类型,amount|blacklist|time',
`value` varchar(64) NOT NULL COMMENT '检查规则值, 1000, 2-5, true',
`description` varchar(2000) DEFAULT NULL COMMENT '描述',
`status` int(4) NOT NULL DEFAULT '0' COMMENT '状态描述。0，正常检查，其他状态。',
`gmt_creator` varchar(30) NOT NULL DEFAULT 't' COMMENT '创建人',
`gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`gmt_updater` varchar(30) NOT NULL DEFAULT 't' COMMENT '更新人',
PRIMARY KEY (`id`),
UNIQUE KEY `uq_rule_key` (`rule_key`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='检查规则表';


CREATE TABLE `transaction` (
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据id',
`transaction_id` varchar(128) NOT NULL COMMENT '交易的原始ID',
`account` varchar(64) NOT NULL COMMENT '交易账户',
`amount` varchar(128) NOT NULL COMMENT '交易金额',
`transaction_time` timestamp NOT NULL COMMENT '交易时间',
`description` varchar(2000) DEFAULT NULL COMMENT '交易备注说明',
`status` int(4) NOT NULL DEFAULT '0' COMMENT '状态描述。0，正常检查，其他状态。',
`gmt_creator` varchar(30) NOT NULL DEFAULT 't' COMMENT '创建人',
`gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`gmt_updater` varchar(30) NOT NULL DEFAULT 't' COMMENT '更新人',
PRIMARY KEY (`id`),
UNIQUE KEY `uq_transaction_id` (`transaction_id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='交易检查记录表';


CREATE TABLE `alert_record` (
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据id',
`notify_user` varchar(64) NOT NULL COMMENT '通知到的人',
`destination` varchar(64) DEFAULT NULL COMMENT '触达目标',
`type` varchar(8) NOT NULL COMMENT '通知方式',
`transaction_id` varchar(128) NOT NULL COMMENT '交易对应的ID',
`account` varchar(128) NOT NULL COMMENT '交易金额',
`amount` varchar(128) NOT NULL COMMENT '交易金额',
`content` varchar(2000) NOT NULL COMMENT '通知的内容',
`status` int(4) NOT NULL DEFAULT '0' COMMENT '状态描述。0，正常检查，其他状态。',
`gmt_creator` varchar(30) NOT NULL DEFAULT 't' COMMENT '创建人',
`gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`gmt_updater` varchar(30) NOT NULL DEFAULT 't' COMMENT '更新人',
PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='告警通知记录表';


CREATE TABLE `notify_user` (
`id` bigint NOT NULL AUTO_INCREMENT COMMENT '数据id',
`user` varchar(64) NOT NULL COMMENT '通知到的人',
`name` varchar(64) NOT NULL COMMENT '对应的name',
`destination` varchar(64) DEFAULT NULL COMMENT '触达目标',
`type` varchar(8) NOT NULL COMMENT '通知方式',
`status` int(4) NOT NULL DEFAULT '0' COMMENT '状态描述。0，正常检查，其他状态。',
`gmt_creator` varchar(30) NOT NULL DEFAULT 't' COMMENT '创建人',
`gmt_created` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
`gmt_updated` timestamp NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新时间',
`gmt_updater` varchar(30) NOT NULL DEFAULT 't' COMMENT '更新人',
PRIMARY KEY (`id`),
UNIQUE KEY `uq_user` (`user`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci COMMENT='通知人员表';