package com.mytijian.mediator.company.migrate.service;

import java.util.Calendar;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class AppTest {

	public static void main(String[] args) {
		ApplicationContext context = new ClassPathXmlApplicationContext(
				"classpath:applicationContext.xml");
		MigrateService migrateService = (MigrateService) context
				.getBean("migrateService");
		migrateService.init();

		long start = Calendar.getInstance().getTimeInMillis();
		while (migrateService.migrate()) {
			migrateService.migrate();
		}
		
		System.out.println("历史数据迁移时间 ："
				+ (Calendar.getInstance().getTimeInMillis() - start));
		
		migrateService.changedMigrateData();
	}
}