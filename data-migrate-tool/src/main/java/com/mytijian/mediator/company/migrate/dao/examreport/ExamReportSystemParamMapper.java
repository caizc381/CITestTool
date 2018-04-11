package com.mytijian.mediator.company.migrate.dao.examreport;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.report.model.ExamReportSystemParam;
import com.mytijian.sharding.datasource.DataSource;
import com.mytijian.sharding.datasource.DynamicDataSource;

@DynamicDataSource(DataSource.SHARDING_EAXM_REPORT)
@Repository("examReportSystemParamMapper")
public interface ExamReportSystemParamMapper {

	/**
	 * 查询系统参数
	 * @param paramKey
	 * @param hospitalId
	 * @return
	 */
	ExamReportSystemParam selectSysParams(@Param("paramKey") String paramKey, @Param("hospitalId") Integer hospitalId);
	
	/**
	 * 更新系统参数
	 * @param sysParam
	 * @return
	 */
	int updateSysParam(ExamReportSystemParam sysParam);

}
