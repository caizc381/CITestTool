package com.mytijian.mediator.company.migrate.service;

/**
 * 订正表数据
 * 
 * @author yuefengyang
 *
 */
public interface CorrectService {
	/**
	 * 根据company_id 和 hospital_id查询tb_hospital_company的id，
	 * 更新tb_company_apply_log.new_company_id、gmt_created、gmt_modified
	 */
	void updateCompanyApplyLog();

	/**
	 * 更新tb_crm_his_company_relation的1585单位
	 */
	void correctCrmHisCompanyFor1585();

	/**
	 * 更新tb_crm_his_company_relation的普通单位
	 */
	void correctCrmHisCompany();
}
