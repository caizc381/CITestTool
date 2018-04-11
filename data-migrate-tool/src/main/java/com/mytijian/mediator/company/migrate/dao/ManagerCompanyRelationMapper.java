package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerCompanyRelationDO;

@Repository("managerCompanyRelationMapper")
public interface ManagerCompanyRelationMapper {
	
	List<ManagerCompanyRelationDO>  selectAll();
	
	List<ManagerCompanyRelationDO>  selectNewCompanyIdIsNull();
	
	List<ManagerCompanyRelationDO> selectNewCompanyIdIsNotNull();
	
	void update(ManagerCompanyRelationDO managerCompanyRelationDO);

	List<Integer> selectByOrganizationId(@Param("organizationId") Integer organizationId);

	void insertGuestCompany(ManagerCompanyRelationDO managerCompanyRelationDO);

	List<Integer> selectPlatformMamager();

	void deleteByManagerIdNewCompanyIdAndHospitalId(@Param("organizationId") Integer organizationId,
			@Param("managerId") Integer managerId,@Param("newCompanyId") Integer newCompanyId);
	
	List<ManagerCompanyRelationDO> selectByManagerId(@Param("managerId") Integer managerId);
}
