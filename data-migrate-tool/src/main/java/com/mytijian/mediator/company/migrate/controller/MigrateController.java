package com.mytijian.mediator.company.migrate.controller;

import java.util.Calendar;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mytijian.mediator.company.migrate.service.ManagerCompanyMigrateService;
import com.mytijian.mediator.company.migrate.service.MigrateService;
import com.mytijian.mediator.company.migrate.task.SchedulerManager;

@Controller
public class MigrateController {

	@Resource(name = "migrateService")
	private MigrateService migrateService;
	
	@Resource(name = "managerCompanyMigrateService")
	private ManagerCompanyMigrateService managerCompanyMigrateService;
	
	@Resource(name = "schedulerManager")
	private SchedulerManager schedulerManager;
	
	@RequestMapping(value = "/start", method = RequestMethod.GET)
	@ResponseBody
	public String start(){
		schedulerManager.startJob();
		return "start job success";
	}
	
	@RequestMapping(value = "/status", method = RequestMethod.GET)
	@ResponseBody
	public String status() {
		return schedulerManager.getTaskStatus();
	}

	@RequestMapping(value = "/migrate", method = RequestMethod.GET)
	@ResponseBody
	public String migrate() {
		long initStart = Calendar.getInstance().getTimeInMillis();
		migrateService.init();
		String info = "init data done :"
				+ (Calendar.getInstance().getTimeInMillis() - initStart)
				+ "ms;";

		long start = Calendar.getInstance().getTimeInMillis();
		while (migrateService.migrate()) {
			migrateService.migrate();
		}

		info += "company data migrate done : "
				+ (Calendar.getInstance().getTimeInMillis() - start) + "ms";

		return info;

	}

	/**
	 * 迁移tb_manager_company_relation
	 * 
	 * @return
	 */
	@RequestMapping(value = "/migrateManagerCompanyRelation", method = RequestMethod.GET)
	@ResponseBody
	public String migrateManagerCompanyRelation() {
		long initStart = Calendar.getInstance().getTimeInMillis();
		managerCompanyMigrateService.migrate();
		return "migrate done"
				+ (Calendar.getInstance().getTimeInMillis() - initStart)
				+ "ms;";

	}
	
	/**
	 * 散客单位设置更新
	 */
	@RequestMapping(value = "/updateGuestCompanySetting", method = RequestMethod.GET)
	@ResponseBody
	public void updateGuestCompanySetting(){
		migrateService.updateOrganizationGuestCompanySetting();
	}
	
	@RequestMapping(value = "/addOnlineGuestCompanyManagerRealation", method = RequestMethod.GET)
	@ResponseBody
	public void addOnlineGuestCompanyManagerRealation(){
		
		migrateService.initManagerOnlineGustCompanyRealation();
	}
	
	@RequestMapping(value = "/migratePlatformCompanyToChannel", method = RequestMethod.GET)
	@ResponseBody
	public void migratePlatformCompanyToChannel(
			@RequestParam(value = "mtjkOrgId") Integer mtjkOrgId) {
		migrateService.migratePlatformCompanyToChannel(mtjkOrgId);
	}

	/* 清洗tb_channel_company data，将196修改为相对应的渠道商id
	 * @return
	 */
	@RequestMapping(value = "/cleanChannelCompanyData", method = RequestMethod.GET)
	@ResponseBody
	public String cleanChannelCompanyData(){
		managerCompanyMigrateService.cleanData();
		return "clean channelCompanyData success";
	}
}
