package com.mytijian.mediator.company.migrate.correct.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mytijian.mediator.company.migrate.correct.dao.dataobj.CrmHisCompanyDO;

public interface CrmHisCompanyMapper {

	/**
	 * 查询1585单位
	 * 
	 * @return
	 */
	List<CrmHisCompanyDO> selectGuestCompany();

	/**
	 * 更新1585的new_company_id
	 * 
	 * @param id
	 */
	void updateNewCompanyIdFor1585(@Param("id") Integer id,
			@Param("newCompanyId") Integer newCompanyId);
	
	/**
	 * 更新普通单位的new_company_id
	 */
	void updateNewCompanyId();
}
