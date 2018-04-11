package com.mytijian.admin.web.vo.resource;

import com.mytijian.resource.dto.GeneralManagerDto;
import com.mytijian.resource.dto.OrganizationDto;

public class OrganizationManagerVo {
	
	private OrganizationDto organization;
	
	private GeneralManagerDto manager;

	public OrganizationDto getOrganization() {
		return organization;
	}

	public void setOrganization(OrganizationDto organization) {
		this.organization = organization;
	}

	public GeneralManagerDto getManager() {
		return manager;
	}

	public void setManager(GeneralManagerDto manager) {
		this.manager = manager;
	}

}
