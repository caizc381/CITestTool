package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.ExamCompanyDO;

@Repository("examCompanyMapper")
public interface ExamCompanyMapper {
	
	List<ExamCompanyDO> selectByLastPrimaryKeyId(
			@Param(value = "lastPrimaryKeyId") Integer lastPrimaryKeyId,
			@Param(value = "limit") Integer limit);

	ExamCompanyDO selectById(@Param(value = "id") Integer id);

}
