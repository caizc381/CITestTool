package com.mytijian.mediator.company.migrate.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import com.mytijian.mediator.company.migrate.dao.dataobj.AccountRoleDO;
import com.mytijian.mediator.company.migrate.dao.dataobj.ManagerAccountCompanyRelationDO;

@Repository("accountRoleMapper")
public interface AccountRoleMapper {
	
	List<AccountRoleDO> selectByAccountId(@Param("accountId")Integer accountId);
	
	/**
	 * 查询出角色是体检中心操作员和体检中心主管的客户经理
	 * @return
	 */
	List<AccountRoleDO> selectAllCrmManager();
	
	/**
	 * 查询客户经理所属的机构
	 * @param accountId
	 * @return
	 */
	Integer selectManagerBelongOrganization(@Param("accountId")Integer accountId);
	
	/**
	 * 判断客户经理是否允许现场付款
	 * @param managerId
	 * @return
	 */
	Integer selectIsSitePaymanagerId(@Param("managerId")Integer managerId);
	
	/**
	 * 查询客户经理的挂账单位
	 * @param managerId
	 * @return
	 */
	List<ManagerAccountCompanyRelationDO> selectManagerAccountCompanyByManagerId(@Param("managerId")Integer managerId);
}
  