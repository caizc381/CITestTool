package com.mytijian.admin.web.facade.task;

import java.util.List;

import com.mytijian.admin.web.vo.AgentTaskVo;
import com.mytijian.resource.model.Hospital;

/**
 * 任务管理门面接口
 * 
 * @author yuefengyang
 *
 */
public interface AgentTaskFacade {

	/**
	 * 根据医院id获取任务详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	List<AgentTaskVo> getAgentTaskByHospId(Integer hospitalId);

	/**
	 * 获取配置过任务的医院
	 * 
	 * @return
	 */
	List<Hospital> getHospListHasTask();
	
	/**
	 * 根据关键词搜索医院,医院id或者名称
	 * @param keyWords
	 * @return
	 */
	List<Hospital> searchHosp(String keyWords);
	
	/**
	 * 获取省份的医院列表
	 * @param provinceIds
	 * @param hosptalName
	 * @param currPage
	 * @param pageSize
	 * @return
	 */
	List<Hospital> listManageTaskHospitals(List<Integer> provinceIds, String hosptalName, Integer currPage, Integer pageSize);
	
	/**
	 * 获取省份的医院管理的医院总数
	 * @param employeeId
	 * @param hosptalName
	 * @return
	 */
	int countProvinceManageTaskHospitals(List<Integer> provinceIds, String hosptalName);
	
	/**
	 * 获取职工部门省份
	 * @param employeeId 职工Id
	 * @return
	 */
	List<Integer> listEmployeeDepartmentProvinceIds(Integer employeeId);

}
