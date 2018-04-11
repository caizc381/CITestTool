package com.mytijian.admin.web.vo.resource;
public class ChannelContactVo {
	 /**
     * email
     */
    private String email;
    
    /**
     * 通知是否发邮件: 选中给该邮件发送同时,未选中不发
     */
    private Boolean itemMessage=false;
    
    /**
    * 联系人id
    */
   private Integer contactId;

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public Boolean getItemMessage() {
		return itemMessage;
	}

	public void setItemMessage(Boolean itemMessage) {
		this.itemMessage = itemMessage;
	}

	public Integer getContactId() {
		return contactId;
	}

	public void setContactId(Integer contactId) {
		this.contactId = contactId;
	}
   
   
   
}
