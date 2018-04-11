package com.mytijian.mediator.company.migrate.dao.dataobj.examreport;

public class OrderConditionDO {

	private Integer newCompanyId;
	
	private Integer oldCompanyId;
	
	private Integer managerId;
	
	private Integer operatorId;
	
	private Integer ownerId;
	
	private Integer fromSite;
	
	private int source;

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getOldCompanyId() {
		return oldCompanyId;
	}

	public void setOldCompanyId(Integer oldCompanyId) {
		this.oldCompanyId = oldCompanyId;
	}

	public Integer getManagerId() {
		return managerId;
	}

	public void setManagerId(Integer managerId) {
		this.managerId = managerId;
	}

	public Integer getOperatorId() {
		return operatorId;
	}

	public void setOperatorId(Integer operatorId) {
		this.operatorId = operatorId;
	}

	public Integer getOwnerId() {
		return ownerId;
	}

	public void setOwnerId(Integer ownerId) {
		this.ownerId = ownerId;
	}

	public Integer getFromSite() {
		return fromSite;
	}

	public void setFromSite(Integer fromSite) {
		this.fromSite = fromSite;
	}

	public int getSource() {
		return source;
	}

	public void setSource(int source) {
		this.source = source;
	}

}
