package com.cjx.monitor.jingsu.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.bson.types.ObjectId;

@Entity
public class User {
	@Id
	private String id;
	private String name;
	private String pwd;
	private String username;
	private String mobile;
	private Date addedTime;
	private Date changedTime;

	public User(String name, String pwd, String username, String mobile) {
		super();
		this.id = new ObjectId().toHexString();
		this.name = name;
		this.pwd = pwd;
		this.username = username;
		this.mobile = mobile;
		this.addedTime = new Date();
		this.changedTime = this.addedTime;
	}

	public User() {
		super();
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public Date getAddedTime() {
		return addedTime;
	}

	public void setAddedTime(Date addedTime) {
		this.addedTime = addedTime;
	}

	public Date getChangedTime() {
		return changedTime;
	}

	public void setChangedTime(Date changedTime) {
		this.changedTime = changedTime;
	}

}
