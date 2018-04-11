package com.mytijian.admin.web.vo.hospital;

import java.util.Map;

import com.mytijian.organization.model.OrganizationMediatorInfo;
import com.mytijian.organization.model.Sign;
import com.mytijian.resource.model.Hospital;

public class HospitalSignVO {
	
	private Hospital hospital;
	
	/**
	 * 对接厂商
	 */
	private String cooperateCompany;
	
	/**
	 * 对接方式-0自对接，1：厂家对接	
	 */
	private Integer cooperateType;
	
	/**
	 * 签约协议类型 1：服务协议 2：保密协议 3：代收协议 4：折扣协议
	 */
	private Map<Integer,Sign> signMap;
	
	/**
	 * 体检科主任
	 */
	private OrganizationMediatorInfo examMediator;
	
	/**
	 * 厂商联系人
	 */
	private OrganizationMediatorInfo hisMediator;
	
	/**
	 * 信息科主任
	 */
	private OrganizationMediatorInfo infoMediator;
	
	/**
	 * 对接备注
	 */
	private String coopCompanyRemark;
	
	/**
	 * 对接费用
	 */
	private Integer mediatorPrice;

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getCooperateCompany() {
		return cooperateCompany;
	}

	public void setCooperateCompany(String cooperateCompany) {
		this.cooperateCompany = cooperateCompany;
	}

	public Integer getCooperateType() {
		return cooperateType;
	}

	public void setCooperateType(Integer cooperateType) {
		this.cooperateType = cooperateType;
	}

	public Map<Integer, Sign> getSignMap() {
		return signMap;
	}

	public void setSignMap(Map<Integer, Sign> signMap) {
		this.signMap = signMap;
	}

	public OrganizationMediatorInfo getExamMediator() {
		return examMediator;
	}

	public void setExamMediator(OrganizationMediatorInfo examMediator) {
		this.examMediator = examMediator;
	}

	public OrganizationMediatorInfo getHisMediator() {
		return hisMediator;
	}

	public void setHisMediator(OrganizationMediatorInfo hisMediator) {
		this.hisMediator = hisMediator;
	}

	public OrganizationMediatorInfo getInfoMediator() {
		return infoMediator;
	}

	public void setInfoMediator(OrganizationMediatorInfo infoMediator) {
		this.infoMediator = infoMediator;
	}

	public String getCoopCompanyRemark() {
		return coopCompanyRemark;
	}

	public void setCoopCompanyRemark(String coopCompanyRemark) {
		this.coopCompanyRemark = coopCompanyRemark;
	}

	public Integer getMediatorPrice() {
		return mediatorPrice;
	}

	public void setMediatorPrice(Integer mediatorPrice) {
		this.mediatorPrice = mediatorPrice;
	}
	


	
}
