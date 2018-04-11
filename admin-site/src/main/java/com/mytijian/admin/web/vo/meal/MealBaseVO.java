package com.mytijian.admin.web.vo.meal;

import java.util.List;

import com.mytijian.pulgin.mybatis.pagination.Page;

/**
 * 
 * @author huangwei
 *
 */
public class MealBaseVO {
	
	private List<MealVO> mealVOList;
	
	private Page page;

	public List<MealVO> getMealVOList() {
		return mealVOList;
	}

	public void setMealVOList(List<MealVO> mealVOList) {
		this.mealVOList = mealVOList;
	}

	public Page getPage() {
		return page;
	}

	public void setPage(Page page) {
		this.page = page;
	}
	
	
}
