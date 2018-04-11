package com.mytijian.mediator.company.migrate.dao.dataobj.user;

/**
 * Created by Administrator on 2017/5/12.
 */
public class AccountRelationSuper extends DomainObjectBase {
    private Integer id;

    private Integer managerId;

    private Integer companyId;

    // 体检单位
    private Integer newCompanyId;

    // 机构ID
    private Integer organizationId;

    // 机构类型
    private Integer organizationType;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }

    public Integer getCompanyId() {
        return companyId;
    }

    public void setCompanyId(Integer companyId) {
        this.companyId = companyId;
    }

    public Integer getNewCompanyId() {
        return newCompanyId;
    }

    public void setNewCompanyId(Integer newCompanyId) {
        this.newCompanyId = newCompanyId;
    }

    public Integer getOrganizationId() {
        return organizationId;
    }

    public void setOrganizationId(Integer organizationId) {
        this.organizationId = organizationId;
    }

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }
}
