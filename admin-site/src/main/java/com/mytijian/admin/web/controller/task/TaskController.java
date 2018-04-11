package com.mytijian.admin.web.controller.task;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.collections.CollectionUtils;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import com.mytijian.admin.dao.base.dataobject.PageUtils;
import com.mytijian.admin.web.facade.task.impl.AgentTaskFacadeImpl;
import com.mytijian.admin.web.util.CommonUtil;
import com.mytijian.admin.web.util.Page;
import com.mytijian.admin.web.util.ShiroUtils;
import com.mytijian.admin.web.vo.AgentTaskVo;
import com.mytijian.mediator.api.enums.AgentTaskStatus;
import com.mytijian.mediator.api.service.MediatorAgentTaskService;
import com.mytijian.resource.model.Hospital;

/**
 * 
 * 类TaskController.java的实现描述：TODO 类实现描述 
 * @author yf 2017年4月7日 下午4:03:51
 */
@Controller
@RequestMapping(value = "/task")
public class TaskController {

	@Resource(name = "agentTaskFacade")
	private AgentTaskFacadeImpl agentTaskFacade;

	@Resource(name = "mediatorAgentTaskService")
	private MediatorAgentTaskService mediatorAgentTaskService;
	
	/**
	 * 返回医院列表
	 * 
	 * @return
	 */
	@RequestMapping(value = "/hospListHasTask", method = RequestMethod.GET)
	@ResponseBody
	public List<Hospital> getHospListHasTask() {
		return agentTaskFacade.getHospListHasTask();
	}

	/**
	 * 查询某医院任务详情
	 * 
	 * @param hospitalId
	 * @return
	 */
	@RequestMapping(value = "/list/{hospitalId}", method = RequestMethod.GET)
	@ResponseBody
	public List<AgentTaskVo> getTasksByHospId(@PathVariable Integer hospitalId) {
		return agentTaskFacade.getAgentTaskByHospId(hospitalId);
	}

	/**
	 * 初始化医院任务
	 * 
	 * @param hospitalId
	 */
	@RequestMapping(value = "/addHospTask", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	public void addHospTask(
			@RequestParam(value = "hospitalId", required = true) Integer hospitalId,
			@RequestParam(value = "provinceId", required = true) Integer provinceId) {

		mediatorAgentTaskService.initHospTask(hospitalId, provinceId);
	}
	
	/**
	 * 打开或者关闭任务
	 * 
	 * @param hospitalId
	 * @param taskCmd
	 * @status status
	 */
	@RequestMapping(value = "/openOrCloseTask", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@RequiresPermissions("sys:hospitalTask:update")
	public void openOrCloseTask(
			@RequestParam(value = "hospitalId", required = true) Integer hospitalId,
			@RequestParam(value = "taskCmd", required = true) String taskCmd,
			@RequestParam(value = "status", required = true) Boolean status) {

		mediatorAgentTaskService.updateAgentTaskByHsId(taskCmd, hospitalId,
				status ? AgentTaskStatus.Open.getCode()
						: AgentTaskStatus.Closed.getCode());
	}
	
	@RequestMapping(value = "/searchHosp", method = RequestMethod.GET)
	@ResponseBody
	public PageUtils searchHosp(
			@RequestParam(value = "keywords", required = true) String keywords, Integer currPage, Integer pageSize) {
	
		Page page = CommonUtil.getPage(currPage, pageSize);
		pageSize = page.getPageSize();
		currPage = page.getCurrPage();
		List<Hospital> hospitals = agentTaskFacade.searchHosp(keywords);
		int total = 0;
		if (CollectionUtils.isNotEmpty(hospitals)) {
			total = hospitals.size();
		}

		return new PageUtils(hospitals, total, pageSize, currPage);
	}
	
	/**
	 * 更新任务配置
	 * @param hospitalId
	 * @param taskCmd
	 * @param crontabExp
	 * @param taskParam
	 */
	@RequestMapping(value = "/updateTask", method = RequestMethod.POST)
	@ResponseStatus(value = HttpStatus.OK)
	@RequiresPermissions("sys:hospitalTask:edit")
	public void updateTask(
			@RequestParam(value = "hospitalId") Integer hospitalId,
			@RequestParam(value = "taskCmd") String taskCmd,
			@RequestParam(value = "cronExpression") String crontabExp,
			@RequestParam(value = "taskParam") String taskParam) {
		//  检查cron exp是否合法
		int expectedParts = 6;
		String[] parts = crontabExp.split("\\s+"); 
		if (parts.length != expectedParts) {
			throw new IllegalArgumentException(String.format(
					"cron expression不合法: [%s]", crontabExp));
		}
		mediatorAgentTaskService.updateTask(hospitalId, taskCmd, taskParam,
				crontabExp);
	}

	
	/*********************************************************************/
	
	/**
	 * 返回医院列表
	 * @return
	 */
	@RequestMapping(value = "/hospitals")
	@RequiresPermissions("sys:hospitalTask:list")
	@ResponseBody
	public PageUtils taskHospitalList(String hospitalName, Integer hospitalId, Integer currPage, Integer pageSize) {
		Page page = CommonUtil.getPage(currPage, pageSize);
		int offset = page.getOffset();
		pageSize = page.getPageSize();
		currPage = page.getCurrPage();
		List<Hospital> hospitals = null;
		// 获取职工Id
		int employeeId = ShiroUtils.getUserId();
		
		List<Integer> provinceIds = agentTaskFacade.listEmployeeDepartmentProvinceIds(employeeId);
		int total = agentTaskFacade.countProvinceManageTaskHospitals(provinceIds, hospitalName);
		if (total > 0) {
			hospitals = agentTaskFacade.listManageTaskHospitals(provinceIds, hospitalName, offset, pageSize);
		}
		return new PageUtils(hospitals, total, pageSize, currPage);
	}
	
}
