package com.mytijian.admin.web.vo.examitem;

import java.io.Serializable;
import java.util.List;

public class ExamItemSpeciesVo implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -3214927405167487648L;

	/**
	 * 总数
	 */
	private Integer totalCount;
	
	/**
	 * 废除条数
	 */
	private Integer invaildCount;
	
	/**
	 * 标准单项列表
	 */
	private List<?> list;

	public Integer getTotalCount() {
		return totalCount;
	}

	public void setTotalCount(Integer totalCount) {
		this.totalCount = totalCount;
	}

	public Integer getInvaildCount() {
		return invaildCount;
	}

	public void setInvaildCount(Integer invaildCount) {
		this.invaildCount = invaildCount;
	}

	public List<?> getList() {
		return list;
	}

	public void setList(List<?> list) {
		this.list = list;
	}
	
}
