package com.mytijian.admin.shop.model;

import java.util.List;

public class ErrorHospital {
	private String orgName;

	private List<ErrorAttr> errorAttrs;

	private String exDescription;

	public String getExDescription() {
		return exDescription;
	}

	public void setExDescription(String exDescription) {
		this.exDescription = exDescription;
	}

	public String getOrgName() {
		return orgName;
	}

	public void setOrgName(String orgName) {
		this.orgName = orgName;
	}

	public List<ErrorAttr> getErrorAttrs() {
		return errorAttrs;
	}

	public void setErrorAttrs(List<ErrorAttr> errorAttrs) {
		this.errorAttrs = errorAttrs;
	}

	public  static class ErrorAttr {
		/**
		 * 错误属性名
		 */
		private String name;
		/**
		 * 错误描述
		 */
		private String errorDesc;

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}

		public String getErrorDesc() {
			return errorDesc;
		}

		public void setErrorDesc(String errorDesc) {
			this.errorDesc = errorDesc;
		}
	}

}
