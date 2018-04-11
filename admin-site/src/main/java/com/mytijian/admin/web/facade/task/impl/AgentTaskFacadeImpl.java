package com.mytijian.admin.web.facade.task.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Service;

import com.google.common.collect.Lists;
import com.mytijian.admin.api.address.model.Province;
import com.mytijian.admin.api.address.service.AreaService;
import com.mytijian.admin.api.address.service.ProvinceService;
import com.mytijian.admin.api.rbac.model.Department;
import com.mytijian.admin.api.rbac.model.DepartmentEmployee;
import com.mytijian.admin.api.rbac.service.DepartmentEmployeeService;
import com.mytijian.admin.api.rbac.service.DepartmentService;
import com.mytijian.admin.web.facade.task.AgentTaskFacade;
import com.mytijian.admin.web.util.ShiroUtils;
import com.mytijian.admin.web.vo.AgentTaskVo;
import com.mytijian.mediator.agentTask.model.AgentTaskConfig;
import com.mytijian.mediator.api.enums.AgentTaskStatus;
import com.mytijian.mediator.api.model.MediatorAgentTaskModel;
import com.mytijian.mediator.api.service.MediatorAgentTaskService;
import com.mytijian.mediator.api.service.param.ListTaskHospitalQuery;
import com.mytijian.resource.model.Hospital;
import com.mytijian.resource.service.HospitalService;

@Service("agentTaskFacade")
public class AgentTaskFacadeImpl implements AgentTaskFacade {

	Logger logger = LoggerFactory.getLogger(AgentTaskFacadeImpl.class);

	@Resource(name = "mediatorAgentTaskService")
	private MediatorAgentTaskService mediatorAgentTaskService;

	@Resource(name = "hospitalService")
	private HospitalService hospitalService;
	
	@Resource(name = "departmentEmployeeService")
	private DepartmentEmployeeService departmentEmployeeService;
	
	@Resource(name = "departmentService")
	private DepartmentService departmentService;

	@Resource(name = "provinceService")
	private ProvinceService provinceService;
	
	@Resource(name = "areaService")
	private AreaService areaService;
	
	@Override
	public List<AgentTaskVo> getAgentTaskByHospId(Integer hospitalId) {
		List<MediatorAgentTaskModel> configedTaskList = mediatorAgentTaskService
				.getTasksByHospId(hospitalId);

		List<AgentTaskConfig> taskConfigs = mediatorAgentTaskService
				.getAgentTaskConfigList();
		
		Hospital hosp = resolveHosp(hospitalId);
		// 初始化该医院的所有预定义的任务，默认状态为关闭
		List<AgentTaskVo> defaultTaskList = initTasks(taskConfigs, hosp);

		for (MediatorAgentTaskModel sourceModel : configedTaskList) {

			AgentTaskVo targetModel = defaultTaskList
					.stream()
					.filter(p -> p.getTaskCmd()
							.equals(sourceModel.getTaskCmd())).findFirst()
					.get();

			// 拷贝任务是否开启等其他参数
			BeanUtils.copyProperties(sourceModel, targetModel);

		}

		return defaultTaskList;
	}

	private List<AgentTaskVo> initTasks(List<AgentTaskConfig> taskList,
			Hospital hosp) {
		List<AgentTaskVo> defaultTaskList = new ArrayList<AgentTaskVo>();
		for (AgentTaskConfig taskConfig : taskList) {
			AgentTaskVo defaultVo = new AgentTaskVo();
			defaultVo.setHospital(hosp);
			defaultVo.setHospitalId(hosp.getId());
			defaultVo.setTaskCmd(taskConfig.getTaskCmd());
			defaultVo.setTaskName(taskConfig.getTaskName());
			defaultVo.setStatus(AgentTaskStatus.Closed.getCode()); // 默认设置为关闭
			defaultTaskList.add(defaultVo);
		}
		return defaultTaskList;
	}

	private Hospital resolveHosp(Integer hospId) {
		Hospital hospital = hospitalService.getHospitalById(hospId);
		Hospital targetHosp = new Hospital();
		targetHosp.setId(hospId);

		if (hospital != null) {
			targetHosp.setName(hospital.getName());
		} else {
			targetHosp.setName("未找到医院");
		}
		return targetHosp;
	}

	@Override
	public List<Hospital> getHospListHasTask() {
		return hospitalService.getHospitalsByIds(mediatorAgentTaskService
				.getHospitalIdHasTask());
	}

	@Override
	public List<Hospital> searchHosp(String keyWords) {
		//List<Hospital> totalList = getHospListHasTask();
		
		List<Integer> provinceIds = listEmployeeDepartmentProvinceIds(ShiroUtils.getUserId());
		List<Hospital> totalList = listManageTaskHospitals(provinceIds, null, null, null);
		
		if (StringUtils.isEmpty(keyWords)) {
			return totalList;
		}

		Integer hospId = null;
		try {
			hospId = Integer.parseInt(keyWords);
		} catch (NumberFormatException e) {
			// do nothing
		}

		if (hospId != null) {
			List<Hospital> result = new ArrayList<Hospital>();
			for (Hospital hospital : totalList) {
				if (hospital.getId() == hospId
						|| hospital.getName().contains(String.valueOf(hospId))) {
					result.add(hospital);
				}
			}
			return result;
		} else {
			totalList = totalList.stream().filter(p -> p.getName().contains(keyWords))
					.collect(Collectors.toList());
			return totalList;
		}
	}
	
	@Override
	public int countProvinceManageTaskHospitals(List<Integer> provinceIds, String hospitalName) {
		ListTaskHospitalQuery listTaskHospitalQuery = getListTaskHospitalQuery(hospitalName, null, null,
				provinceIds);

		return mediatorAgentTaskService.countTaskHospitals(listTaskHospitalQuery);
	}

	@Override
	public List<Hospital> listManageTaskHospitals(List<Integer> provinceIds, String hospitalName, Integer offset,
			Integer limit) {

		ListTaskHospitalQuery listTaskHospitalQuery = getListTaskHospitalQuery(hospitalName, offset, limit,
				provinceIds);
		List<Integer> hospitalIds = mediatorAgentTaskService.listTaskHospitalIds(listTaskHospitalQuery);
		return hospitalService.getHospitalsByIds(hospitalIds);
	}

	private List<Province> listEmployDepartmentProvinces(Integer employeeId) {

		List<Province> provinces = Lists.newArrayList();
		// 获取用户所在部门
		DepartmentEmployee departmentEmployee = departmentEmployeeService.getByEmployeeId(employeeId);
		if (departmentEmployee == null || departmentEmployee.getDepartmentId() == null) {
			return provinces;
		}

		// 获取部门信息
		Department department = departmentService.getWithParentIdById(departmentEmployee.getDepartmentId());

		if (department.getProvinceId() != null) {
			// 只有一个省份
			Province province = provinceService.getByIdOrProvinceId(department.getProvinceId(), null);
			provinces.add(province);
		} else if (department.getAreaId() != null) {
			// 区域下面的全部省份
			List<Province> ps = provinceService.listByAreaId(department.getAreaId());
			if (CollectionUtils.isNotEmpty(ps)) {
				provinces.addAll(ps);
			}
		}

		return provinces;
	}

	@Override
	public List<Integer> listEmployeeDepartmentProvinceIds(Integer employeeId) {
		// 获取用户部门所支持的省份
		List<Province> provinces = listEmployDepartmentProvinces(employeeId);
		List<Integer> provinceIds = Lists.newArrayList();
		if (CollectionUtils.isNotEmpty(provinces) && provinces.size() > 0) {
			provinces.forEach(province -> {
				provinceIds.add(province.getProvinceId());
			});
		}
		return provinceIds;
	}

	private ListTaskHospitalQuery getListTaskHospitalQuery(String hospitalName, Integer offset, Integer limit,
			List<Integer> provinceIds) {
		ListTaskHospitalQuery listTaskHospitalQuery = new ListTaskHospitalQuery();
		listTaskHospitalQuery.setHospitalName(hospitalName);
		listTaskHospitalQuery.setProvinceIds(provinceIds);
		listTaskHospitalQuery.setOffset(offset);
		listTaskHospitalQuery.setLimit(limit);
		return listTaskHospitalQuery;
	}

}
