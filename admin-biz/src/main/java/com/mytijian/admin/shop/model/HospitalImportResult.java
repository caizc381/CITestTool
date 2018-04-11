package com.mytijian.admin.shop.model;

import java.io.Serializable;
import java.util.List;

import com.mytijian.admin.shop.model.ErrorHospital;

/**
 * @author king
 */
public class HospitalImportResult implements Serializable{

	/**
	 * 如果成功，成功医院数量successHospitalNumber有值，失败失败医院列表有值
	 */
	private boolean isSuccess;

	private int successHospitalNumber;

	private List<Integer> successHospitalIds;

	private List<ErrorHospital> errorHospitals;

	private String importSerial;

	private int totalHospital;

	public int getTotalHospital() {
		return totalHospital;
	}

	public void setTotalHospital(int totalHospital) {
		this.totalHospital = totalHospital;
	}

	public boolean isSuccess() {
		return isSuccess;
	}

	public void setSuccess(boolean success) {
		isSuccess = success;
	}

	public String getImportSerial() {
		return importSerial;
	}

	public void setImportSerial(String importSerial) {
		this.importSerial = importSerial;
	}

	public List<Integer> getSuccessHospitalIds() {
		return successHospitalIds;
	}

	public void setSuccessHospitalIds(List<Integer> successHospitalIds) {
		this.successHospitalIds = successHospitalIds;
	}

	public boolean getIsSuccess() {
		return isSuccess;
	}

	public void setIsSuccess(boolean isSuccess) {
		this.isSuccess = isSuccess;
	}

	public int getSuccessHospitalNumber() {
		return successHospitalNumber;
	}

	public void setSuccessHospitalNumber(int successHospitalNumber) {
		this.successHospitalNumber = successHospitalNumber;
	}

	public List<ErrorHospital> getErrorHospitals() {
		return errorHospitals;
	}

	public void setErrorHospitals(List<ErrorHospital> errorHospitals) {
		this.errorHospitals = errorHospitals;
	}

}
