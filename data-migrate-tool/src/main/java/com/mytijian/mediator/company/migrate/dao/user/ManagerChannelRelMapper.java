package com.mytijian.mediator.company.migrate.dao.user;

import org.springframework.stereotype.Repository;

@Repository("managerChannelRelMapper")
public interface ManagerChannelRelMapper {

	/**
	 * 根据平台客户经理查询渠道商id集合
	 * @param platformManagerId
	 * @return
	 */
	Integer selectChannelIdsByPlatformManagerId(int platformManagerId);
	
}
