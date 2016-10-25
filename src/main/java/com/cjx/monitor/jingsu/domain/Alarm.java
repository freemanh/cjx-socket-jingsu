package com.cjx.monitor.jingsu.domain;

import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.Id;

import org.bson.types.ObjectId;

@Entity
public class Alarm {
	@Id
	private String id;
	private String deviceId;
	private String deviceName;
	private String content;
	private Date addedTime;

	public Alarm(String deviceId, String deviceName, String content) {
		super();
		this.id = new ObjectId().toHexString();
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.content = content;
		this.addedTime = new Date();
	}

	public Alarm() {
		super();
	}

	@Override
	public String toString() {
		return "Alarm [id=" + id + ", deviceId=" + deviceId + ", deviceName="
				+ deviceName + ", content=" + content + ", addedTime="
				+ addedTime + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDeviceId() {
		return deviceId;
	}

	public void setDeviceId(String deviceId) {
		this.deviceId = deviceId;
	}

	public String getDeviceName() {
		return deviceName;
	}

	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Date getAddedTime() {
		return addedTime;
	}

	public void setAddedTime(Date addedTime) {
		this.addedTime = addedTime;
	}

}
