package com.mytijian.mediator.company.migrate.dao.user;

import com.mytijian.mediator.company.migrate.dao.dataobj.user.ImportGroupId;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImportGroupMapper {

	/**
	 * 查询tb_import_group_seq表中new_company_id为null的记录
	 * @return
	 */
	List<ImportGroupId> selectimportGroupSeqsExamCompanyIdIsNull(@Param("nowId") Integer nowId, @Param(value = "limit") Integer limit);

	/**
	 * 填充tb_import_group_seq表中new_company_id、organization_type字段
	 */
	int updateImportGroupSeq(ImportGroupId importGroupId);
}
