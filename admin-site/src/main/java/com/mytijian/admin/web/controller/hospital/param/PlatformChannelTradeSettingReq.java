package com.mytijian.admin.web.controller.hospital.param;

public class PlatformChannelTradeSettingReq {
	
	/**
	 * 体检中心id
	 */
	private int hospitalId;
	/**
	 * 平台渠道散客折扣
	 */
	private Double platformChannelGuestDiscount;
	/**
	 * 平台渠道单位折扣
	 */
	private Double platformChannelCompDiscount;
	
	public int getHospitalId() {
		return hospitalId;
	}
	public void setHospitalId(int hospitalId) {
		this.hospitalId = hospitalId;
	}
	public Double getPlatformChannelGuestDiscount() {
		return platformChannelGuestDiscount;
	}
	public void setPlatformChannelGuestDiscount(Double platformChannelGuestDiscount) {
		this.platformChannelGuestDiscount = platformChannelGuestDiscount;
	}
	public Double getPlatformChannelCompDiscount() {
		return platformChannelCompDiscount;
	}
	public void setPlatformChannelCompDiscount(Double platformChannelCompDiscount) {
		this.platformChannelCompDiscount = platformChannelCompDiscount;
	}
	
	@Override
	public String toString() {
		return "PlatformChannelTradeSettingVO [hospitalId=" + hospitalId + ", platformChannelGuestDiscount="
				+ platformChannelGuestDiscount + ", platformChannelCompDiscount=" + platformChannelCompDiscount + "]";
	}

}
