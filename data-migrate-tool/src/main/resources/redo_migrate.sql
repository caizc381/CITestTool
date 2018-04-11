-- ------------------------------单位----------------------------------
delete from `tb_hospital_company`;
alter table tb_hospital_company AUTO_INCREMENT = 5300000;

delete from `tb_channel_company`;
alter table tb_channel_company AUTO_INCREMENT = 4300000;

delete from `tb_platform_company` where id > 5;
alter table tb_platform_company AUTO_INCREMENT = 4200000;
-- 重置迁移日志
update tb_company_migrate_log set `last_pk_id` = 782 ,init_done = 'todo',migrate_done = 'todo',`gmt_modified` = now();
-- 清理单位操作日志表
delete from `tb_company_operation_log`;

-- 设置new_company_id为空
update tb_crm_his_company_relation set new_company_id = null;
update tb_company_apply_log set new_company_id = null;
update tb_manager_company_relation set new_company_id = null;
-- ------------------------------单位----------------------------------

--user
--重置tb_import_group_seq表
UPDATE tb_import_group_seq SET `new_company_id` = NULL, `organization_type` = NULL;
UPDATE tb_account_relationship SET `new_company_id` = NULL, `organization_type` = NULL, organization_id = NULL;
UPDATE tb_account_relationship_fail SET `new_company_id` = NULL, `organization_type` = NULL, organization_id = NULL;
-- 重置迁移日志
update tb_user_migrate_log set `last_pk_id` = 0 ,init_done = 'todo',migrate_done = 'todo',`gmt_modified` = now();


--order
--重置tb_order
-- 重置迁移日志
update tb_order_migrate_log set `last_pk_id` = 0 ,init_done = 'todo',migrate_done = 'todo',`gmt_modified` = now();