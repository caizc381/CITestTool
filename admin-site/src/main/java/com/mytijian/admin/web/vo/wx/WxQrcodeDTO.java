package com.mytijian.admin.web.vo.wx;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.mytijian.wx.model.WxArticle;

public class WxQrcodeDTO {

	private String title;
	private Integer codeAction;
	private MultipartFile image;
	private String type;
	private List<WxArticle> articles;
	private String scene;
	private String msg;
	private String mediaId;
	
	
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public List<WxArticle> getArticles() {
		return articles;
	}
	public void setArticles(List<WxArticle> articles) {
		this.articles = articles;
	}
	public String getScene() {
		return scene;
	}
	public void setScene(String scene) {
		this.scene = scene;
	}
	public Integer getCodeAction() {
		return codeAction;
	}
	public void setCodeAction(Integer codeAction) {
		this.codeAction = codeAction;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMediaId() {
		return mediaId;
	}
	public void setMediaId(String mediaId) {
		this.mediaId = mediaId;
	}
	public MultipartFile getImage() {
		return image;
	}
	public void setImage(MultipartFile image) {
		this.image = image;
	}
}
