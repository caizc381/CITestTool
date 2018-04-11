package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.CompanyOperationLogDO;



@Repository("companyOperationLogMapper")
public interface CompanyOperationLogMapper {
	
	/**
	 * 插入
	 * @param operationDO
	 */
	void insert(CompanyOperationLogDO operationDO);
	
	/**
	 * 根据is_completed查询
	 * @param completed
	 * @return
	 */
	List<CompanyOperationLogDO> selectByCompleted(@Param("completed") String completed);
	
	/**
	 * 更新is_completed
	 * @param id
	 * @param completed
	 */
	void updateIsCompleted(@Param("id") Integer id, @Param("completed") String completed);
	
	/**
	 * 批量更新is_completed
	 * @param ids
	 * @param completed
	 */
	void batchUpdateIsCompleted(@Param("idList") List<Integer> ids, @Param("completed") String completed);
}
