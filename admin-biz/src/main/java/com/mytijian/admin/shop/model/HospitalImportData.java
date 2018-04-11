package com.mytijian.admin.shop.model;



public class HospitalImportData{
	
	@ExcelFieldName(fieldName = "外部机构编码")
	private String outerOrgCode;
	
	@ExcelFieldName(fieldName = "体检中心名称" ,errorTip = "不能为空")
	private String name;
	
	@ExcelFieldName(fieldName = "医院等级",errorTip = "不能为空,且需要填写正确")
	private String level;
	
	@ExcelFieldName(fieldName = "省" ,errorTip = "必填，请填写完整。格式：'浙江省'")
	private String province;
	
	@ExcelFieldName(fieldName = "市",errorTip = "必填，请填写完整。格式：'杭州市'")
	private String city;
	
	@ExcelFieldName(fieldName = "区/县",errorTip = "必填，请填写完整。格式：'西湖区'")
	private String district;
	
	@ExcelFieldName(fieldName = "详细地址",errorTip = "必填")
	private String address;

	@ExcelFieldName(fieldName = "纬度",errorTip = "必填，小于90度")
	private String latitude;

	@ExcelFieldName(fieldName = "经度",errorTip = "必填，小于180度")
	private String longitude;
	
	@ExcelFieldName(fieldName = "交通信息")
	private String trafficInfo;

	@ExcelFieldName(fieldName = "管理员姓名",errorTip = "必填")
	private String managerName;
	
	@ExcelFieldName(fieldName = "管理员性别",errorTip = "必填，选项：'男'或者'女'")
	private String gender;
	
	@ExcelFieldName(fieldName = "管理员手机号",errorTip = "必填，请填写正确的手机号")
	private String mobile;
	
	@ExcelFieldName(fieldName = "CRM账号",errorTip = "必填，且账号不能被使用")
	private String crmUsername;
	
	@ExcelFieldName(fieldName = "年业务量")
	private String businessAmount;
	
	@ExcelFieldName(fieldName = "体检电话",errorTip = "必填，格式正确")
	private String phone;
	
	@ExcelFieldName(fieldName = "团检电话",errorTip = "必填，格式正确")
	private String groupExamTel;
	
//	@ExcelFieldName(fieldName = "客服电话")
//	private String serviceTel;
//	
//	@ExcelFieldName(fieldName = "技术支持")
//	private String technicalTel;
	
	@ExcelFieldName(fieldName = "医院标签",errorTip = "必填，不能为空")
	private String keywords;
	
	@ExcelFieldName(fieldName = "周一",errorTip = "必填，请填写'是'或'否'")
	private String monday;
	
	@ExcelFieldName(fieldName = "周二",errorTip = "必填，请填写'是'或'否'")
	private String tuesday;
	
	@ExcelFieldName(fieldName = "周三",errorTip = "必填，请填写'是'或'否'")
	private String wednesday;
	
	@ExcelFieldName(fieldName = "周四",errorTip = "必填，请填写'是'或'否'")
	private String thursday;
	
	@ExcelFieldName(fieldName = "周五",errorTip = "必填，请填写'是'或'否'")
	private String friday;
	
	@ExcelFieldName(fieldName = "周六",errorTip = "必填，请填写'是'或'否'")
	private String saturday;
	
	@ExcelFieldName(fieldName = "周日" ,errorTip = "必填，请填写'是'或'否'")
	private String sunday;
	
	@ExcelFieldName(fieldName = "提供早餐", errorTip = "必填，请填写'提供'或'不提供'")
	private String isBreakfast;
	
	@ExcelFieldName(fieldName = "简介")
	private String briefIntro;
	
	@ExcelFieldName(fieldName = "详细介绍")
	private String detailIntro;
	
	@ExcelFieldName(fieldName = "体检须知",errorTip = "必填")
	private String examNotice;
	
	@ExcelFieldName(fieldName = "体检开始时间",errorTip="必填，格式'12:00' 且小于体检结束时间")
	private String examStartTime;
	
	@ExcelFieldName(fieldName = "体检结束时间",errorTip="必填，格式'12:00' ")
	private String examEndTime;
	
	@ExcelFieldName(fieldName = "抽血开始时间",errorTip="必填，格式'12:00' 且小于抽血结束时间")
	private String drawBloodStart;
	
	@ExcelFieldName(fieldName = "抽血结束时间",errorTip="必填，格式'12:00'")
	private String drawBloodEnd;
	
	@ExcelFieldName(fieldName = "时段名称" ,errorTip = "必填，不能为空")
	private String hospitalPeriod;
	
	@ExcelFieldName(fieldName = "可约人数",errorTip = "必填，大于0的整数")
	private String limitNum;
	
	@ExcelFieldName(fieldName = "短域名",errorTip = "必填，且不能已被使用")
	private String url;

	public String getOuterOrgCode() {
		return outerOrgCode;
	}

	public void setOuterOrgCode(String outerOrgCode) {
		this.outerOrgCode = outerOrgCode;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getLevel() {
		return level;
	}

	public void setLevel(String level) {
		this.level = level;
	}

	public String getProvince() {
		return province;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getDistrict() {
		return district;
	}

	public void setDistrict(String district) {
		this.district = district;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getLatitude() {
		return latitude;
	}

	public void setLatitude(String latitude) {
		this.latitude = latitude;
	}

	public String getLongitude() {
		return longitude;
	}

	public void setLongitude(String longitude) {
		this.longitude = longitude;
	}

	public String getTrafficInfo() {
		return trafficInfo;
	}

	public void setTrafficInfo(String trafficInfo) {
		this.trafficInfo = trafficInfo;
	}

	public String getManagerName() {
		return managerName;
	}

	public void setManagerName(String managerName) {
		this.managerName = managerName;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCrmUsername() {
		return crmUsername;
	}

	public void setCrmUsername(String crmUsername) {
		this.crmUsername = crmUsername;
	}

	public String getBusinessAmount() {
		return businessAmount;
	}

	public void setBusinessAmount(String businessAmount) {
		this.businessAmount = businessAmount;
	}

	public String getPhone() {
		return phone;
	}

	public void setPhone(String phone) {
		this.phone = phone;
	}

	public String getGroupExamTel() {
		return groupExamTel;
	}

	public void setGroupExamTel(String groupExamTel) {
		this.groupExamTel = groupExamTel;
	}

	public String getKeywords() {
		return keywords;
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	public String getMonday() {
		return monday;
	}

	public void setMonday(String monday) {
		this.monday = monday;
	}

	public String getTuesday() {
		return tuesday;
	}

	public void setTuesday(String tuesday) {
		this.tuesday = tuesday;
	}

	public String getWednesday() {
		return wednesday;
	}

	public void setWednesday(String wednesday) {
		this.wednesday = wednesday;
	}

	public String getThursday() {
		return thursday;
	}

	public void setThursday(String thursday) {
		this.thursday = thursday;
	}

	public String getFriday() {
		return friday;
	}

	public void setFriday(String friday) {
		this.friday = friday;
	}

	public String getSaturday() {
		return saturday;
	}

	public void setSaturday(String saturday) {
		this.saturday = saturday;
	}

	public String getSunday() {
		return sunday;
	}

	public void setSunday(String sunday) {
		this.sunday = sunday;
	}

	public String getIsBreakfast() {
		return isBreakfast;
	}

	public void setIsBreakfast(String isBreakfast) {
		this.isBreakfast = isBreakfast;
	}

	public String getBriefIntro() {
		return briefIntro;
	}

	public void setBriefIntro(String briefIntro) {
		this.briefIntro = briefIntro;
	}

	public String getDetailIntro() {
		return detailIntro;
	}

	public void setDetailIntro(String detailIntro) {
		this.detailIntro = detailIntro;
	}

	public String getExamNotice() {
		return examNotice;
	}

	public void setExamNotice(String examNotice) {
		this.examNotice = examNotice;
	}

	public String getExamStartTime() {
		return examStartTime;
	}

	public void setExamStartTime(String examStartTime) {
		this.examStartTime = examStartTime;
	}

	public String getExamEndTime() {
		return examEndTime;
	}

	public void setExamEndTime(String examEndTime) {
		this.examEndTime = examEndTime;
	}

	public String getDrawBloodStart() {
		return drawBloodStart;
	}

	public void setDrawBloodStart(String drawBloodStart) {
		this.drawBloodStart = drawBloodStart;
	}

	public String getDrawBloodEnd() {
		return drawBloodEnd;
	}

	public void setDrawBloodEnd(String drawBloodEnd) {
		this.drawBloodEnd = drawBloodEnd;
	}

	public String getHospitalPeriod() {
		return hospitalPeriod;
	}

	public void setHospitalPeriod(String hospitalPeriod) {
		this.hospitalPeriod = hospitalPeriod;
	}

	public String getLimitNum() {
		return limitNum;
	}

	public void setLimitNum(String limitNum) {
		this.limitNum = limitNum;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	@Override
	public String toString() {
		return "HospitalImportData [outerOrgCode=" + outerOrgCode + ", name=" + name + ", level=" + level
				+ ", province=" + province + ", city=" + city + ", district=" + district + ", address=" + address
				+ ", latitude=" + latitude + ", longitude=" + longitude + ", trafficInfo=" + trafficInfo
				+ ", managerName=" + managerName + ", gender=" + gender + ", mobile=" + mobile + ", crmUsername="
				+ crmUsername + ", businessAmount=" + businessAmount + ", phone=" + phone + ", groupExamTel="
				+ groupExamTel + ", keywords=" + keywords + ", monday=" + monday + ", tuesday=" + tuesday
				+ ", wednesday=" + wednesday + ", thursday=" + thursday + ", friday=" + friday + ", saturday="
				+ saturday + ", sunday=" + sunday + ", isBreakfast=" + isBreakfast + ", briefIntro=" + briefIntro
				+ ", detailIntro=" + detailIntro + ", examNotice=" + examNotice + ", examStartTime=" + examStartTime
				+ ", examEndTime=" + examEndTime + ", drawBloodStart=" + drawBloodStart + ", drawBloodEnd="
				+ drawBloodEnd + ", hospitalPeriod=" + hospitalPeriod + ", limitNum=" + limitNum + ", url=" + url + "]";
	}
	
}
