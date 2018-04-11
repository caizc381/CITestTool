CREATE TABLE `tb_company_migrate_log` (
  `id` int(11) NOT NULL AUTO_INCREMENT,
  `table_name` varchar(100) DEFAULT NULL COMMENT '表名',
  `min_pk_id` int(11) DEFAULT NULL COMMENT '最小的主键id',
  `last_pk_id` int(11) DEFAULT NULL COMMENT '上一次读取的主键id',
  `gmt_created` datetime DEFAULT NULL,
  `gmt_modified` datetime DEFAULT NULL,
  `limit` int(11) DEFAULT NULL COMMENT '每次读取多少条',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8;

INSERT INTO `tb_company_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit`)
VALUES
	(1, 'tb_exam_company', 783, 782, NULL, NULL, 100);

CREATE TABLE `tb_platform_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '单位id',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `name` varchar(200) NOT NULL COMMENT '单位展示名',
  `description` varchar(2000) DEFAULT NULL COMMENT '简述',
  `pinyin` varchar(2000) DEFAULT NULL COMMENT '拼音',
  `init` varchar(50) DEFAULT NULL COMMENT 'hospital:建医院站点时初始化;channel:建渠道商时初始化',
  `is_deleted` tinyint(1) DEFAULT '0' COMMENT '删除标记，0:为删除，1:删除',
  `employee_import` tinyint(1) DEFAULT '0' COMMENT '是否员工号导入，0:否 1:是',
  `tb_exam_company_id` int(11) DEFAULT NULL COMMENT '原单位表主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='平台单位表';

CREATE TABLE `tb_hospital_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '体检单位id',
  `name` varchar(200) NOT NULL COMMENT '单位名称',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `platform_company_id` int(11) NOT NULL COMMENT '平台单位id',
  `organization_id` int(11) NOT NULL COMMENT '机构id',
  `discount` double(10,9) DEFAULT '1.000000000' COMMENT '单位折扣',
  `show_report` tinyint(1) DEFAULT '1' COMMENT '体检报告对客户是否可见 1：是 0：否',
  `show_invoice` tinyint(1) DEFAULT '1' COMMENT '是否显示发票 1：是 0：否',
  `employee_import` tinyint(1) NOT NULL DEFAULT '0' COMMENT '是否员工号导入，0:否 1:是',
  `settlement_mode` tinyint(1) DEFAULT NULL COMMENT '结算方式，0：按项目，1：按人数',
  `his_name` varchar(255) DEFAULT NULL COMMENT 'his单位名称',
  `advance_export_order` tinyint(1) DEFAULT '1' COMMENT '是否可以提前导出订单',
  `send_exam_sms` tinyint(1) DEFAULT '0' COMMENT '是否发送检前短信，1:是，0:否',
  `send_exam_sms_days` int(11) DEFAULT '1' COMMENT '检前短信提前几天发送',
  `pinyin` varchar(2000) DEFAULT NULL COMMENT '拼音',
  `employee_prefix` varchar(100) DEFAULT NULL COMMENT '员工号前缀',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0:未删除，1:已删除',
  `tb_exam_company_id` int(11) DEFAULT NULL COMMENT '原单位表主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='医院单位';

CREATE TABLE `tb_channel_company` (
  `id` int(11) NOT NULL AUTO_INCREMENT COMMENT '体检单位id',
  `name` varchar(200) NOT NULL COMMENT '单位名称',
  `gmt_created` datetime DEFAULT NULL COMMENT '创建时间',
  `gmt_modified` datetime DEFAULT NULL COMMENT '更新时间',
  `platform_company_id` int(11) NOT NULL COMMENT '平台单位id',
  `organization_id` int(11) NOT NULL COMMENT '机构id',
  `discount` double(10,9) DEFAULT '1.000000000' COMMENT '单位折扣',
  `show_invoice` tinyint(1) DEFAULT '1' COMMENT '是否显示发票 1：是 0：否',
  `settlement_mode` tinyint(1) DEFAULT NULL COMMENT '结算方式，0：按项目，1：按人数',
  `send_exam_sms` tinyint(1) DEFAULT '0' COMMENT '是否发送检前短信，1:是，0:否',
  `send_exam_sms_days` int(11) DEFAULT '1' COMMENT '检前短信提前几天发送',
  `is_deleted` tinyint(1) NOT NULL DEFAULT '0' COMMENT '删除标记，0:未删除，1:已删除',
  `pinyin` varchar(50) DEFAULT NULL COMMENT 'pinyin',
  `tb_exam_company_id` int(11) DEFAULT NULL COMMENT '原单位表主键',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='渠道商单位';

-- 平台单位初始化
INSERT INTO `tb_platform_company` (`id`, `gmt_created`, `gmt_modified`, `name`, `description`, `pinyin`, `init`, `is_deleted`, `employee_import`, `tb_exam_company_id`)
VALUES
	(1, '2017-03-29 06:45:40', '2017-03-29 06:45:40', '个人网上预约', NULL, 'grwsyy', 'hospital', 0, 0, NULL),
	(2, '2017-03-29 06:45:40', '2017-03-29 06:45:40', '现场散客', NULL, 'xcsk', 'hospital', 0, 0, NULL),
	(3, '2017-03-29 06:45:40', '2017-03-29 06:45:40', '每天健康', NULL, 'mtjk', 'hospital', 0, 0, NULL),
	(4, '2017-03-29 06:45:40', '2017-03-29 06:45:40', '个人网上预约', NULL, 'grwsyy', 'channel', 0, 0, NULL),
	(5, '2017-03-29 06:45:40', '2017-03-29 06:45:40', '散客单位', NULL, 'skdw', 'channel', 0, 0, NULL);

--user
CREATE TABLE `tb_user_migrate_log` (
  `id` INT(11) NOT NULL AUTO_INCREMENT,
  `table_name` VARCHAR(100) DEFAULT NULL COMMENT '表名',
  `min_pk_id` INT(11) DEFAULT NULL COMMENT '最小的主键id',
  `last_pk_id` INT(11) DEFAULT NULL COMMENT '上一次读取的主键id',
  `gmt_created` DATETIME DEFAULT NULL,
  `gmt_modified` DATETIME DEFAULT NULL,
  `limit_size` INT(11) DEFAULT NULL COMMENT '每次读取多少条',
  `init_done` VARCHAR(11) DEFAULT NULL COMMENT '是否初始化完成,todo/done',
  `migrate_done` VARCHAR(11) DEFAULT NULL COMMENT '是否历史数据迁移完成,todo/done',
  PRIMARY KEY (`id`)
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='用户模块迁移日志表';

INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
  VALUES	(1, 'tb_account_relationship', 1, 1, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
  VALUES	(2, 'tb_account_relationship_fail', 1, 1, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
  VALUES	(3, 'tb_import_group_seq', 1, 1, NOW(), NOW(), 100, 'todo', 'todo');