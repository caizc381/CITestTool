package com.mytijian.admin.shop.service;

import com.mytijian.admin.shop.param.InitDataReq;
import com.mytijian.organization.dto.OrganizationManagerDto;

/**
 * 类HospitalInitDataService.java的实现描述：医院化服务接口，收拢医院初始化数据业务
 * @author ljx 2018年1月31日 上午10:35:33
 */
public interface HospitalInitDataService {

	/**
	 * 初始化数据聚合接口
	 * @param initDataReq
	 * @return
	 */
	public OrganizationManagerDto initData(InitDataReq initDataReq);
	
}
