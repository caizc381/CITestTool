package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyHospitalRelDO;

@Repository("examCompanyHospitalRelMapper")
public interface ExamCompanyHospitalRelMapper {
	// select * from tb_hospital_exam_company_relation where company_id = ?
	List<ExamCompanyHospitalRelDO> selectByCompanyId(
			@Param("companyId") Integer companyId);
	
	
}
