package com.mytijian.mediator.company.migrate.correct.dao;

import org.springframework.stereotype.Repository;

@Repository("companyApplyLogMapper")
public interface CompanyApplyLogMapper {
	/**
	 * 订正单位申请日志
	 */
	void correctCompanyApplyLog();
	
	
}
