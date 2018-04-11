package com.mytijian.mediator.company.migrate.task;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;

import javax.annotation.Resource;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mytijian.mediator.company.migrate.service.examreport.ExamReportService;
import com.mytijian.pool.ThreadPoolManager;
import com.mytijian.pulgin.mybatis.pagination.Page;

@Component("migrateReportNewCompnyIdTask")
@DisallowConcurrentExecution
public class MigrateReportNewCompnyIdJob implements Job{

	private Logger logger = LoggerFactory
			.getLogger(MigrateReportNewCompnyIdJob.class);
	
	@Resource(name = "examReportService")
	private ExamReportService examReportService;
	
	private ExecutorService executor = ThreadPoolManager.newFixedThreadPool(9);
	
	@Override
	public void execute(JobExecutionContext context) throws JobExecutionException {
		executor.execute(() -> {
			Page page = new Page();
			page.setCurrentPage(1);
			page.setPageSize(100);
			//更新tb_group_report_summary_base表
			examReportService.updateGroupSummaryNewCompanyId(page);
		});
		List<Integer> list = Arrays.asList(0,1,2,3);
		for(int i=0; i < list.size(); i++){
			int suffix = list.get(i);
			executor.execute(() -> {
				updateExamReportNewCompanyId(suffix);
			});
			executor.execute(() -> {
				updateGroupReportNewCompanyId(suffix);
			});
		}
	}

	private void updateExamReportNewCompanyId(int suffix) {
		Page page = new Page();
		page.setCurrentPage(1);
		page.setPageSize(50);
		String tableName = "tb_examreport_" + suffix;
		logger.info("更新体检报告新单位id， tableName:", tableName);
		try{
			examReportService.updateReportNewCompanyId(tableName, page);
		} catch(Exception ex) {
			logger.error("更新体检报告新单位id失败，tableName：{}", "tb_examreport_" + suffix, ex);
		}
	}
	
	private void updateGroupReportNewCompanyId(int suffix){
		logger.info("更新团检报告2张表新单位id");
		Page page = new Page();
		page.setCurrentPage(1);
		page.setPageSize(100);
		//更新tb_report_age_base表
		examReportService.updateGroupBaseNewCompanyId("tb_report_age_base_" + suffix, page);
		//更新tb_report_exceptional_base表
		examReportService.updateGroupBaseNewCompanyId("tb_report_exceptional_base_" + suffix, page);
	}

}
