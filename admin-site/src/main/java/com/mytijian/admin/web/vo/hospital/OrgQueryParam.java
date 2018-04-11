package com.mytijian.admin.web.vo.hospital;

import com.mytijian.organization.param.OrganizationQuery;

public class OrgQueryParam {

	/**
	 * 私有查询条件
	 */
	private HospitalPrivateQuery privateQuery;
	
	private OrganizationQuery organizationQuery;

	public OrganizationQuery getOrganizationQuery() {
		return organizationQuery;
	}

	public void setOrganizationQuery(OrganizationQuery organizationQuery) {
		this.organizationQuery = organizationQuery;
	}

	public HospitalPrivateQuery getPrivateQuery() {
		return privateQuery;
	}

	public void setPrivateQuery(HospitalPrivateQuery privateQuery) {
		this.privateQuery = privateQuery;
	}

}
