package com.mytijian.mediator.company.migrate.dao.examreport;

import java.util.List;

import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.examreport.OrderConditionDO;

@Repository
public interface ConditionMapper {

	List<Integer> selectAllPlatformManager();
	
	List<Integer> selectAllMCompanyId();
	
	OrderConditionDO selectOrderByOrderNum(String orderNum);
}
