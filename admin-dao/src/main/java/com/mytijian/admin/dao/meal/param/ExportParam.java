package com.mytijian.admin.dao.meal.param;

import java.util.List;

public class ExportParam {
	
	private List<Integer> mealIdList;
	
	private List<Integer> hospitalIdList;
	
	private boolean isExportAll;

	public List<Integer> getMealIdList() {
		return mealIdList;
	}

	public void setMealIdList(List<Integer> mealIdList) {
		this.mealIdList = mealIdList;
	}

	public List<Integer> getHospitalIdList() {
		return hospitalIdList;
	}

	public void setHospitalIdList(List<Integer> hospitalIdList) {
		this.hospitalIdList = hospitalIdList;
	}

	public boolean isExportAll() {
		return isExportAll;
	}

	public void setExportAll(boolean isExportAll) {
		this.isExportAll = isExportAll;
	}
	
	
}
