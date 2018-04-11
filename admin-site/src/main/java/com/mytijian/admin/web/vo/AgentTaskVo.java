package com.mytijian.admin.web.vo;

import com.mytijian.mediator.api.model.MediatorAgentTaskModel;
import com.mytijian.resource.model.Hospital;

/**
 * agent任务模型，页面展示用
 * 
 * @author yuefengyang
 *
 */
public class AgentTaskVo extends MediatorAgentTaskModel {

	/**
	 * 
	 */
	private static final long serialVersionUID = -8967012284713298130L;

	/**
	 * 体检中心,只有id和name
	 */
	private Hospital hospital;

	/**
	 * 任务在页面上的显示名称
	 */
	private String taskName;

	public Hospital getHospital() {
		return hospital;
	}

	public void setHospital(Hospital hospital) {
		this.hospital = hospital;
	}

	public String getTaskName() {
		return taskName;
	}

	public void setTaskName(String taskName) {
		this.taskName = taskName;
	}

}
