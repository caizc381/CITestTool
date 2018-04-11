package com.mytijian.mediator.company.migrate.dao;

import com.mytijian.mediator.company.migrate.dao.dataobj.ChannelCompanyDO;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository("channelCompanyMapper")
public interface ChannelCompanyMapper {
	void insert(ChannelCompanyDO channelCompanyDO);
	
	void insertList(@Param("list") List<ChannelCompanyDO> list);
	
	// TODO hosp id ,exam_company_id-》id
	ChannelCompanyDO selectByExamCompanyId(@Param("examCompanyId") Integer examCompanyId);

    ChannelCompanyDO selectByPlatformCompanyIdAndOrganizationId(@Param("platformCompanyId") Integer platformCompanyId, @Param("organizationId") Integer organizationId);

	// organizationId ,tb_exam_company_id->id
	ChannelCompanyDO selectByExamCompanyIdAndOrganizationId(
			@Param("tbExamCompanyId") Integer tbExamCompanyId,
			@Param("organizationId") Integer organizationId);
	
	// TODO update
	void updateChannelCompany(ChannelCompanyDO channelCompanyDO);
	
	/**
	 * 根据平台单位查询渠道商单位
	 * 
	 * @param platformCompanyId
	 * @return
	 */
	List<ChannelCompanyDO> selectByPlatformCompanyId2(
			@Param("platformCompanyId") Integer platformCompanyId);
	
	List<ChannelCompanyDO> selectChannelGusetCompany(@Param("organizationId") Integer organizationId);
	
	ChannelCompanyDO selectById(@Param("newCompanyId") Integer newCompanyId);
	
	ChannelCompanyDO selectByOrganizationIdAndExamCompanyId(@Param("examCompanyId") Integer examCompanyId,@Param("organizationId") Integer organizationId);
	
}
