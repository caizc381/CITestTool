package com.mytijian.admin.web.facade.hospital;

import java.util.List;

import com.mytijian.admin.web.dto.HospitalQueryDto;
import com.mytijian.admin.web.vo.resource.HospitalCompanyVO;
import com.mytijian.admin.web.vo.resource.AreaVo;
import com.mytijian.admin.web.vo.resource.OrganizationVO;
import com.mytijian.resource.service.hospital.param.HospitalQuery;

/**
 * 医院相关接口
 * 类HospitalFacade.java的实现描述：TODO 类实现描述 
 * @author zhanfei.feng 2017年4月7日 下午4:05:29
 */
public interface HospitalFacade {

	/**
	 * 查询医院
	 * @param hospitalQuery
	 * @return
	 */
	List<OrganizationVO> listHospitalIdAndNameByHospitalQuery(HospitalQuery hospitalQuery);

	AreaVo getAreaVo();

	List<OrganizationVO> listHospital(HospitalQueryDto hospitalQueryDto);

	List<HospitalCompanyVO> listCompany(Integer hospitalId);
}
