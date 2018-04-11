alter table tb_company_apply_log add column new_company_id int(11) comment '新单位id';
-- 根据company_id 和 hospital id查询tb_hospital_company的id，
-- 更新tb_company_apply_log.new_company_id

alter table tb_company_apply_log add gmt_created  datetime comment '创建时间';
alter table tb_company_apply_log add gmt_modified  datetime comment '更新时间';