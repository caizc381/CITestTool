package com.mytijian.admin.web.vo.company.channel;

import java.util.List;

import com.mytijian.company.channel.service.model.ChannelCompany;
import com.mytijian.company.model.ManagerExamCompanyRelation;
import com.mytijian.resource.model.Hospital;

public class ChannelCompanyVO {

	private ChannelCompany channelCompany;

	private Hospital hospital;

	private List<ManagerExamCompanyRelation> companyManagerList;

	public ChannelCompany getChannelCompany() {
		return channelCompany;
	}

	public void setChannelCompany(ChannelCompany channelCompany) {
		this.channelCompany = channelCompany;
	}

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public List<ManagerExamCompanyRelation> getCompanyManagerList() {
		return companyManagerList;
	}

	public void setCompanyManagerList(
			List<ManagerExamCompanyRelation> companyManagerList) {
		this.companyManagerList = companyManagerList;
	}

}
