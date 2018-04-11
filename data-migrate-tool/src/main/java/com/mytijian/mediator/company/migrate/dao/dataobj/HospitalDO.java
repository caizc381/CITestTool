package com.mytijian.mediator.company.migrate.dao.dataobj;

import java.io.Serializable;

public class HospitalDO implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4688169667670803128L;

	private Integer id;

	private String name;

	private Integer organizationType;
	
	private String guestOnline;
	
	private String guestOffline;
	
	private String mGuest;

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

	public Integer getOrganizationType() {
		return organizationType;
	}

	public void setOrganizationType(Integer organizationType) {
		this.organizationType = organizationType;
	}

	public String getGuestOnline() {
		return guestOnline;
	}

	public void setGuestOnline(String guestOnline) {
		this.guestOnline = guestOnline;
	}

	public String getGuestOffline() {
		return guestOffline;
	}

	public void setGuestOffline(String guestOffline) {
		this.guestOffline = guestOffline;
	}

	public String getmGuest() {
		return mGuest;
	}

	public void setmGuest(String mGuest) {
		this.mGuest = mGuest;
	}
	
}
