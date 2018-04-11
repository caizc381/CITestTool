package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalCompanyDO;

@Repository("hospitalCompanyMapper")
public interface HospitalCompanyMapper {

	void insert(HospitalCompanyDO hospitalCompanyDO);

	void insertList(@Param("list") List<HospitalCompanyDO> list);
	
	// organizationId ,tb_exam_company_id->id
	HospitalCompanyDO selectByExamCompanyIdAndOrganizationId(
			@Param("tbExamCompanyId") Integer tbExamCompanyId,
			@Param("organizationId") Integer organizationId);
	
	HospitalCompanyDO selectByOldExamCompanyIdAndOrganizationId(
			@Param("newCompanyId") Integer newCompanyId,
			@Param("organizationId") Integer organizationId);
	
	// TODO update
	List<HospitalCompanyDO> selectByExamCompanyId(@Param("examCompanyId") Integer examCompanyId);
	
	void updateExamCompany(HospitalCompanyDO hospitalCompanyDO);
	
	void updateNameByTbExamCompanyIdAndOrganizationId(
			@Param("name") String name, @Param("pinyin") String pinyin,
			@Param("tbExamCompanyId") Integer tbExamCompanyId,
			@Param("organizationId") Integer organizationId);
	/**
	 * 删除散客单位
	 * @param organizationId
	 */
	void deleteGuestCompany(@Param("organizationId") Integer organizationId );
	
	List<HospitalCompanyDO> selectHospitalGuestCompany(@Param("organizationId") Integer organizationId);
	
	HospitalCompanyDO selectOnlineGuestCompanyByOrganizationId(@Param("organizationId") Integer organizationId);
}
