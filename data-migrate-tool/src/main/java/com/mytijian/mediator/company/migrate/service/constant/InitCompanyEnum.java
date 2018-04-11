package com.mytijian.mediator.company.migrate.service.constant;

import java.util.ArrayList;
import java.util.List;

public enum InitCompanyEnum {
	HOSPITAL_GUEST_ONLINE(1,"个人网上预约",-100),
	HOSPITAL_GUEST_OFFLINE(2,"现场散客",-101),
	HOSPITAL_MTJK(3,"每天健康",-102),
	CHANNEL_GUEST_ONLINE(4,"个人网上预约",-103),
	CHANNEL_GUEST_OFFLINE(5,"散客单位",-104);
	
	private Integer id;
	
	private String name;
	
	/**
	 * 指定一个特殊的老单位id
	 */
	private Integer tbExamCompanyId;
	
	private InitCompanyEnum(Integer id, String name, Integer tbExamCompanyId) {
		this.id = id;
		this.name = name;
		this.tbExamCompanyId = tbExamCompanyId;
	}

	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTbExamCompanyId() {
		return tbExamCompanyId;
	}

	public void setTbExamCompanyId(Integer tbExamCompanyId) {
		this.tbExamCompanyId = tbExamCompanyId;
	}
	
	public static List<Integer> getTbExamComapnyId() {
		InitCompanyEnum[] array = InitCompanyEnum.values();
		List<Integer> list = new ArrayList<Integer>();
		for (InitCompanyEnum e : array) {
			list.add(e.getTbExamCompanyId());
		}

		return list;
	}
	
}
