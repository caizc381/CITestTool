package com.mytijian.admin.web.vo.wx;

import com.mytijian.wx.model.WxArticle;
import com.mytijian.wx.sdk.btn.WxButton;

import java.util.List;

/**
 *
 * @author linzhihao
 */
public class WxConfigVO {
	
	private Integer hospitalId;
	
	private List<WxButton> buttons;
	
	private String hid;
	
	private List<WxArticle> articles;

	public List<WxButton> getButtons() {
		return buttons;
	}

	public void setButtons(List<WxButton> buttons) {
		this.buttons = buttons;
	}

	public Integer getHospitalId() {
		return hospitalId;
	}

	public void setHospitalId(Integer hospitalId) {
		this.hospitalId = hospitalId;
	}

	public String getHid() {
		return hid;
	}

	public void setHid(String hid) {
		this.hid = hid;
	}

	public List<WxArticle> getArticles() {
		return articles;
	}

	public void setArticles(List<WxArticle> articles) {
		this.articles = articles;
	}
	
}
