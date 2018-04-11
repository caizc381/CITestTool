CREATE TABLE `tb_card_migrate_log` (
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
) ENGINE=INNODB DEFAULT CHARSET=utf8 COMMENT='卡模块单位迁移日志表';

-------------------------------------------------------------------------------------

alter table `tb_card_batch` Add column new_company_id int(11) COMMENT '新单位ID';
alter table `tb_card_batch` Add column organization_type tinyint(1) COMMENT '机构类型';
alter table `tb_card_exam_note` Add column new_company_id int(11) COMMENT '新单位ID';
alter table `tb_card_exam_note` Add column organization_type tinyint(1) COMMENT '机构类型';
alter table `tb_manager_card_relation` Add column new_company_id int(11) COMMENT '新单位ID';
alter table `tb_manager_card_relation` Add column organization_type tinyint(1) COMMENT '机构类型';

-------------------------------------------------------------------------------------

INSERT INTO `tb_card_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(1, 'tb_card_batch', 1, 0, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_card_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(2, 'tb_manager_card_relation', 1, 0, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_card_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(3, 'tb_card_exam_note', 1, 0, NOW(), NOW(), 100, 'todo', 'todo');

-------------------------------------------------------------------------------------

--重新迁移
UPDATE tb_card_batch SET `new_company_id` = NULL, `organization_type` = NULL;
UPDATE tb_manager_card_relation SET `new_company_id` = NULL, `organization_type` = NULL;
UPDATE tb_card_exam_note SET `new_company_id` = NULL, `organization_type` = NULL;
update tb_card_migrate_log set `last_pk_id` = 0 ,init_done = 'todo',migrate_done = 'todo',`gmt_modified` = now();