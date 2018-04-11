package com.mytijian.mediator.company.migrate.dao.dataobj.card;

import com.mytijian.util.AssertUtil;
import com.mytijian.util.DateUtils;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by mawanqun on 17/5/26.
 */
public class CardBase implements Serializable {

    private static final long serialVersionUID = 1L;

    private Integer id;

    private Integer companyId;

    private Integer newCompanyId;

    private Integer organizationType;

    private Integer managerId;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
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

    public Integer getOrganizationType() {
        return organizationType;
    }

    public void setOrganizationType(Integer organizationType) {
        this.organizationType = organizationType;
    }

    public Integer getManagerId() {
        return managerId;
    }

    public void setManagerId(Integer managerId) {
        this.managerId = managerId;
    }
}
