package com.mytijian.admin.web.vo.account;

public class ManagerVo {

	/**
	 * accountid
	 */
	private Integer id;

	/**
	 * 用于登录
	 */
	private String username;
	/**
	 * 客户经理姓名
	 */
	private String name;

	private String mobile;

	/**
	 * 挂帐单位
	 */
	private Integer accountCompanyId;
	
	private Boolean isSitePay;


	/**
	 * 专属网址
	 */
	private String identity;

	private Integer hospitalId;

	/**
	 * 搜索词
	 */
	private String searchWord;

	private Integer roleId;
	
	private Integer gender;
	
	/**
	 * 渠道商id
	 */
	private Integer channelId;
	
	public Integer getId() {
		return id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public Integer getAccountCompanyId() {
		return accountCompanyId;
	}

	public void setAccountCompanyId(Integer accountCompanyId) {
		this.accountCompanyId = accountCompanyId;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getSearchWord() {
		return searchWord;
	}

	public void setSearchWord(String searchWord) {
		this.searchWord = searchWord;
	}

	public String getIdentity() {
		return identity;
	}

	public void setIdentity(String identity) {
		this.identity = identity;
	}

	public Boolean getIsSitePay() {
		return isSitePay;
	}

	public void setIsSitePay(Boolean isSitePay) {
		this.isSitePay = isSitePay;
	}

	public Integer getRoleId() {
		return roleId;
	}

	public void setRoleId(Integer roleId) {
		this.roleId = roleId;
	}

	public Integer getGender() {
		return gender;
	}

	public void setGender(Integer gender) {
		this.gender = gender;
	}

	public Integer getChannelId() {
		return channelId;
	}

	public void setChannelId(Integer channelId) {
		this.channelId = channelId;
	}

}
