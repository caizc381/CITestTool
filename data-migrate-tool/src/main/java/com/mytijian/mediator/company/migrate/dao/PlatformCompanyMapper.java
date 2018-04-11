package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.PlatformCompanyDO;

@Repository("platformCompanyMapper")
public interface PlatformCompanyMapper {

	void insert(PlatformCompanyDO pcompDo);
	
	List<PlatformCompanyDO> selectByOrganizationType(@Param("type") String type);
	
	void insertList(@Param("list") List<PlatformCompanyDO> list);
	
	PlatformCompanyDO selectByExamCompanyId(@Param("examCompanyId") Integer examCompanyId);
	
	void updatePlatformCompany(PlatformCompanyDO pcompDo);
}
