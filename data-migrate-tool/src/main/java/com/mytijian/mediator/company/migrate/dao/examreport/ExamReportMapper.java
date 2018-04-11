package com.mytijian.mediator.company.migrate.dao.examreport;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.examreport.ConditionDO;
import com.mytijian.pulgin.mybatis.pagination.Page;
import com.mytijian.sharding.datasource.DataSource;
import com.mytijian.sharding.datasource.DynamicDataSource;

@DynamicDataSource(DataSource.SHARDING_EAXM_REPORT)
@Repository("examReportMapper")
public interface ExamReportMapper {

	List<ConditionDO> selectWithOrderNumByPage(@Param("tableName") String tableName,
			@Param("startIndex") int startIndex, Page page);

	List<ConditionDO> selectGroupDataWithCompanyIdByPage(@Param("tableName") String tableName,
			@Param("startIndex") int startIndex, Page page);

	List<ConditionDO> selectGroupSummaryWithCompanyIdByPage(@Param("startIndex") int startIndex, Page page);

	String selectOrderNumByReportId(@Param("tableName") String tableName, @Param("reportId") int reportId);

	int updateNewCompanyIdById(@Param("tableName") String tableName, @Param("reportId") int reportId,
			@Param("newCompanyId") Integer newCompanyId, @Param("oldCompanyId") Integer oldCompanyId);

}
