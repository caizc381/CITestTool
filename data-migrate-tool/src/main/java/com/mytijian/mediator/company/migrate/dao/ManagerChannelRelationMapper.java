package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerChannelRelationDO;

@Repository("managerChannelRelationMapper")
public interface ManagerChannelRelationMapper {
	
	ManagerChannelRelationDO selectByManagerId(@Param(value = "managerId")Integer managerId);
	
	List<ManagerChannelRelationDO> selectByManagerIds(@Param("managerIds")List<Integer> managerIds);
}
