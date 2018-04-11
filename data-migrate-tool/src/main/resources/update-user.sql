---------user-------------
ALTER TABLE tb_account_relationship ADD COLUMN new_company_id INT(11) COMMENT '单位改造后新单位ID'
, ADD COLUMN organization_id INT(11) COMMENT '机构ID'
, ADD COLUMN organization_type INT(1) COMMENT '机构类型';
ALTER TABLE `tb_account_relationship_fail` ADD COLUMN new_company_id INT(11) COMMENT '单位改造后新单位ID'
, ADD COLUMN organization_id INT(11) COMMENT '机构ID'
, ADD COLUMN organization_type INT(1) COMMENT '机构类型';
ALTER TABLE `tb_import_group_seq` ADD COLUMN new_company_id INT(11) COMMENT '单位改造后新单位ID'
, ADD COLUMN organization_type INT(1) COMMENT '机构类型';

------------删除外键-----------
ALTER TABLE tb_account_relationship DROP FOREIGN KEY tb_account_relationship_ibfk_1;
ALTER TABLE tb_account_relationship DROP FOREIGN KEY tb_account_relationship_ibfk_2;
ALTER TABLE tb_account_relationship DROP FOREIGN KEY tb_account_relationship_ibfk_3;

----------加索引和主键ID---------
ALTER TABLE tb_account_relationship DROP PRIMARY KEY;
ALTER TABLE `tb_account_relationship` ADD UNIQUE UNIQUER_NEW (manager_id, customer_id, TYPE, company_id, new_company_id, organization_type);
ALTER TABLE tb_account_relationship ADD id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT;

ALTER TABLE `tb_account_relationship_fail` ADD INDEX manager_id_new(manager_id, new_company_id, organization_type);

ALTER TABLE tb_import_group_seq DROP PRIMARY KEY;
ALTER TABLE `tb_import_group_seq` ADD UNIQUE UNIQUER_NEW (manager_id, company_id, new_company_id, organization_type);
ALTER TABLE tb_import_group_seq ADD id INT(11) NOT NULL PRIMARY KEY AUTO_INCREMENT;

--------------初始化迁移日志表记录----------------
INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(1, 'tb_account_relationship', 0, 0, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(2, 'tb_account_relationship_fail', 0, 0, NOW(), NOW(), 100, 'todo', 'todo');
INSERT INTO `tb_user_migrate_log` (`id`, `table_name`, `min_pk_id`, `last_pk_id`, `gmt_created`, `gmt_modified`, `limit_size`, `init_done`, `migrate_done`)
VALUES	(3, 'tb_import_group_seq', 0, 0, NOW(), NOW(), 100, 'todo', 'todo');