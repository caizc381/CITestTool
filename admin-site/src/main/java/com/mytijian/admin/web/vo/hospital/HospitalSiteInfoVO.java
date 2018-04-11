package com.mytijian.admin.web.vo.hospital;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mytijian.site.model.SiteResource;

public class HospitalSiteInfoVO {
	
	private Integer templateId;
	
	private String templateName;
	
	private Integer cssId;
	
	private String cssName;
	
	private String url;
	
	private String imageList;
	
	private List<SiteResource> mobileMainBannerRes;
	
	private List<SiteResource> mobileDeputyBannerRes;
	
	private SiteResource logoRes;
	
	private SiteResource qrCodeRes;
	
	private SiteResource coverRes;
	
	private List<SiteResource> environmentRes;
	
	private Integer hospitalId;

	private Boolean isChannelEdit;
	
	private Integer siteId;

	public Integer getTemplateId() {
		return templateId;
	}

	public void setTemplateId(Integer templateId) {
		this.templateId = templateId;
	}

	public Integer getCssId() {
		return cssId;
	}

	public void setCssId(Integer cssId) {
		this.cssId = cssId;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getImageList() {
		return imageList;
	}

	public void setImageList(String imageList) {
		this.imageList = imageList;
	}

	public List<SiteResource> getMobileMainBannerRes() {
		return mobileMainBannerRes;
	}

	public void setMobileMainBannerRes(List<SiteResource> mobileMainBannerRes) {
		this.mobileMainBannerRes = mobileMainBannerRes;
	}

	public List<SiteResource> getMobileDeputyBannerRes() {
		return mobileDeputyBannerRes;
	}

	public void setMobileDeputyBannerRes(List<SiteResource> mobileDeputyBannerRes) {
		this.mobileDeputyBannerRes = mobileDeputyBannerRes;
	}

	public SiteResource getLogoRes() {
		return logoRes;
	}

	public void setLogoRes(SiteResource logoRes) {
		this.logoRes = logoRes;
	}

	public SiteResource getQrCodeRes() {
		return qrCodeRes;
	}

	public void setQrCodeRes(SiteResource qrCodeRes) {
		this.qrCodeRes = qrCodeRes;
	}

	public SiteResource getCoverRes() {
		return coverRes;
	}

	public void setCoverRes(SiteResource coverRes) {
		this.coverRes = coverRes;
	}

	public List<SiteResource> getEnvironmentRes() {
		return environmentRes;
	}

	public void setEnvironmentRes(List<SiteResource> environmentRes) {
		this.environmentRes = environmentRes;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getTemplateName() {
		return templateName;
	}

	public void setTemplateName(String templateName) {
		this.templateName = templateName;
	}

	public String getCssName() {
		return cssName;
	}

	public void setCssName(String cssName) {
		this.cssName = cssName;
	}

	public Integer getSiteId() {
		return siteId;
	}

	public void setSiteId(Integer siteId) {
		this.siteId = siteId;
	}

	public Boolean getIsChannelEdit() {
		return isChannelEdit;
	}

	public void setIsChannelEdit(Boolean channelEdit) {
		isChannelEdit = channelEdit;
	}
}
