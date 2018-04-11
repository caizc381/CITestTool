-- 1、用户新单位id
update `tb_account_relationship` relation set relation.new_company_id = (
	select channel.id from tb_channel_company channel where channel.organization_id = (
		select channel_id from tb_manager_channel_relation where manager_id = relation.manager_id
	) and channel.tb_exam_company_id = (case when relation.company_id = 1585 then -104 else  relation.company_id end)
)
where relation.`manager_id` in (select account_id from tb_account_role where role_id = 4) --  平台客户经理
and relation.`company_id` is not null -- and relation.`company_id` = 1585
and EXISTS (select * from tb_manager_channel_relation where `manager_id` = relation.`manager_id`);

--机构id
update `tb_account_relationship` relation set relation.`organization_id` = (
 select channelrel.`channel_id` from tb_manager_channel_relation  channelrel where channelrel.`manager_id` = relation.`manager_id`
) where relation.`manager_id` in (select account_id from tb_account_role where role_id = 4) --  平台客户经理
and relation.`company_id` is not null -- and relation.`company_id` = 1585
and EXISTS (select * from tb_manager_channel_relation where `manager_id` = relation.`manager_id`);

-- 机构类型
update `tb_account_relationship` relation set relation.`organization_type` = (
 select hosp.organization_type from `tb_hospital` hosp where hosp.id = relation.organization_id
) where relation.`manager_id` in (select account_id from tb_account_role where role_id = 4) --  平台客户经理
and relation.`company_id` is not null -- and relation.`company_id` = 1585
and EXISTS (select * from tb_manager_channel_relation where `manager_id` = relation.`manager_id`);
 

-- 2、套餐
update tb_meal_customized customized set customized.new_company_id = (
select channel.id from `tb_channel_company` channel where channel.organization_id = (select channel_id from tb_manager_channel_relation where `manager_id` = customized.account_id) and
    channel.tb_exam_company_id = (case when customized.company_id = 1585 then -104 else  customized.company_id end)
)
where
customized.account_id in (select account_id from tb_account_role where role_id = 4) --  平台客户经理
and customized.`company_id` is not null -- 单位套餐
and EXISTS (select * from tb_manager_channel_relation where `manager_id` = customized.account_id);
