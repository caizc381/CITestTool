package com.mytijian.admin.web.vo.meal;

import java.io.Serializable;
import java.util.List;

import com.mytijian.admin.web.vo.examitem.ExamItemWithOutIgnoreVo;

public class MealExamitemVO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5503387474922658154L;

	/**
	 * 加项包规则
	 */
	private Integer ruleId;

	/**
	 * 套餐
	 */
	private MealVO meal;

	/**
	 * 套餐体检项列表
	 */
	List<ExamItemWithOutIgnoreVo> mealItemList;

	/**
	 * 新单位id
	 */
	private Integer newCompanyId;

	/**
	 * 客户经理账户id
	 */
	private Integer accountId;

	public Integer getRuleId() {
		return ruleId;
	}

	public void setRuleId(Integer ruleId) {
		this.ruleId = ruleId;
	}

	public MealVO getMeal() {
		return meal;
	}

	public void setMeal(MealVO meal) {
		this.meal = meal;
	}

	public List<ExamItemWithOutIgnoreVo> getMealItemList() {
		return mealItemList;
	}

	public void setMealItemList(List<ExamItemWithOutIgnoreVo> mealItemList) {
		this.mealItemList = mealItemList;
	}

	public Integer getNewCompanyId() {
		return newCompanyId;
	}

	public void setNewCompanyId(Integer newCompanyId) {
		this.newCompanyId = newCompanyId;
	}

	public Integer getAccountId() {
		return accountId;
	}

	public void setAccountId(Integer accountId) {
		this.accountId = accountId;
	}

}
