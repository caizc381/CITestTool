package com.mytijian.mediator.company.migrate.service.examreport;

import com.mytijian.pulgin.mybatis.pagination.Page;

public interface ExamReportService {

	public void updateReportNewCompanyId(String tableName, Page page);
	
	
	public void updateGroupBaseNewCompanyId(String tableName, Page page);
	
	public void updateGroupSummaryNewCompanyId(Page page);
	
}
