package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.HospitalDO;

@Repository("hospitalMapper")
public interface HospitalMapper {
	List<HospitalDO> selectAll();
	
	HospitalDO selectGuestNameByHospId(@Param("hospId") Integer hospId);
	
	HospitalDO selectBaseInfoByHospId(@Param("hospId") Integer hospId);
}
