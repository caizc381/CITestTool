package com.mytijian.mediator.company.migrate.dao.resource;

import org.springframework.stereotype.Repository;

import com.mytijian.resource.model.HospitalSettings;

@Repository("organizationSettingsMapper")
public interface OrganizationSettingsMapper {
	
	HospitalSettings getHospitalSettingByHospitalId(Integer hospitalId);
}
