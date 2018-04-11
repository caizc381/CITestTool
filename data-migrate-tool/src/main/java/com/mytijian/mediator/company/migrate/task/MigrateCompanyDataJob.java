package com.mytijian.mediator.company.migrate.task;

import javax.annotation.Resource;

import org.quartz.DisallowConcurrentExecution;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.mytijian.company.migrate.service.constant.CompanyOperationConstants;
import com.mytijian.mediator.company.migrate.dao.CompanyMigrateLogMapper;
import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyMigrateLogDO;
import com.mytijian.mediator.company.migrate.service.CorrectService;
import com.mytijian.mediator.company.migrate.service.ManagerCompanyMigrateService;
import com.mytijian.mediator.company.migrate.service.MigrateService;

@Component("migrateCompanyDataTask")
@DisallowConcurrentExecution
public class MigrateCompanyDataJob implements Job{

	private Logger logger = LoggerFactory
			.getLogger(MigrateCompanyDataJob.class);

	@Resource(name = "migrateService")
	private MigrateService migrateService;

	@Resource(name = "companyMigrateLogMapper")
	private CompanyMigrateLogMapper companyMigrateLogMapper;
	
	@Resource(name = "managerCompanyMigrateService")
	private ManagerCompanyMigrateService managerCompanyMigrateService;
	
	@Resource(name = "correctService")
	private CorrectService correctService;
	
	@Override
	public void execute(JobExecutionContext context)
			throws JobExecutionException {
//		System.out.println(Calendar.getInstance().getTime());
		CompanyMigrateLogDO migLog = companyMigrateLogMapper
				.selectById(CompanyMigrateLogDO.ID);
		if (CompanyOperationConstants.DONE.equals(migLog.getInitDone())
				&& CompanyOperationConstants.DONE.equals(migLog
						.getMigrateDone())) {
			try {
				migrateService.changedMigrateData();
				// 订正客户经理关系表
				managerCompanyMigrateService.migrate();
				
				// 订正crm his单位关系表
				correctService.correctCrmHisCompany();
				correctService.correctCrmHisCompanyFor1585();
				
				// 订正单位申请表
				correctService.updateCompanyApplyLog();
			} catch (Exception e) {
//				System.out.println("error");
				logger.error("run task error", e);
			}
		} else {
			logger.info("初始化和历史数据未迁移完成，不能读单位操作日志迁移");
		}
		
	}
	
}
