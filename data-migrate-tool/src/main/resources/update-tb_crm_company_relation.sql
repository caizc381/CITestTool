-- 添加字段
alter table tb_crm_his_company_relation add column new_company_id int(11) comment '新单位id';

-- update tb_crm_his_company_relation rel
-- set rel.new_company_id = (select id from `tb_hospital_company` hp where hp.`organization_id` = rel.`hospital_id` and hp.`tb_exam_company_id` = rel.`crm_company_id`)
-- where rel.`new_company_id` is null;

-- 遍历，得到hospital id，查询医院设置表，根据name匹配，找到对应的tb_exam_company_id
-- select * from `tb_crm_his_company_relation` where `crm_company_id` = 1585; 

-- select * from `tb_crm_his_company_relation` where `crm_company_id` = 1585 and `hospital_id` = 4;
-- 2016年网上预约(个人)4444
-- 2016散客aaa
-- 每天健康
-- select `hospital_id`,`guest_online_comp_alias`,
-- `guest_offline_comp_alias`,
-- `m_guest_comp_alias` 
-- from `tb_hospital_settings` where hospital_id = 4;

-- 2016散客aaa
-- 2016年网上预约(个人)4444
-- 每天健康