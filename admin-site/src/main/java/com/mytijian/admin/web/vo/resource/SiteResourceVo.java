package com.mytijian.admin.web.vo.resource;

import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public class SiteResourceVo {

	private Integer mobileTemplateId;
	
	private Integer pcTemplateId;
	
	private Integer cssId;
	
	private String imageList;
	
	private List<MultipartFile> mobileMainBannerList;
	
	private List<MultipartFile> mobileDeputyBannerList;
	
	private MultipartFile logo;
	
	private MultipartFile qrCode;
	
	private MultipartFile cover;
	
	private List<MultipartFile> environmentList;
	
	private Integer hospitalId;

	public Integer getMobileTemplateId() {
		return mobileTemplateId;
	}

	public void setMobileTemplateId(Integer mobileTemplateId) {
		this.mobileTemplateId = mobileTemplateId;
	}

	public Integer getPcTemplateId() {
		return pcTemplateId;
	}

	public void setPcTemplateId(Integer pcTemplateId) {
		this.pcTemplateId = pcTemplateId;
	}

	public Integer getCssId() {
		return cssId;
	}

	public void setCssId(Integer cssId) {
		this.cssId = cssId;
	}

	public String getImageList() {
		return imageList;
	}

	public void setImageList(String imageList) {
		this.imageList = imageList;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public List<MultipartFile> getMobileMainBannerList() {
		return mobileMainBannerList;
	}

	public void setMobileMainBannerList(List<MultipartFile> mobileMainBannerList) {
		this.mobileMainBannerList = mobileMainBannerList;
	}

	public List<MultipartFile> getMobileDeputyBannerList() {
		return mobileDeputyBannerList;
	}

	public void setMobileDeputyBannerList(List<MultipartFile> mobileDeputyBannerList) {
		this.mobileDeputyBannerList = mobileDeputyBannerList;
	}

	public MultipartFile getLogo() {
		return logo;
	}

	public void setLogo(MultipartFile logo) {
		this.logo = logo;
	}

	public MultipartFile getQrCode() {
		return qrCode;
	}

	public void setQrCode(MultipartFile qrCode) {
		this.qrCode = qrCode;
	}

	public MultipartFile getCover() {
		return cover;
	}

	public void setCover(MultipartFile cover) {
		this.cover = cover;
	}

	public List<MultipartFile> getEnvironmentList() {
		return environmentList;
	}

	public void setEnvironmentList(List<MultipartFile> environmentList) {
		this.environmentList = environmentList;
	}

}
