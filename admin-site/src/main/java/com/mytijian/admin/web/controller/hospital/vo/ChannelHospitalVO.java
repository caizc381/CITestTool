package com.mytijian.admin.web.controller.hospital.vo;

import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.model.OrganizationHospitalRelation;

public class ChannelHospitalVO {

    private Hospital hospital;

    private OrganizationHospitalRelation organizationHospitalRelation;

    public Hospital getHospital() {
        return hospital;
    }

    public void setHospital(Hospital hospital) {
        this.hospital = hospital;
    }

    public OrganizationHospitalRelation getOrganizationHospitalRelation() {
        return organizationHospitalRelation;
    }

    public void setOrganizationHospitalRelation(OrganizationHospitalRelation organizationHospitalRelation) {
        this.organizationHospitalRelation = organizationHospitalRelation;
    }
}
