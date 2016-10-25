package com.cjx.monitor.jingsu.domain;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.Id;

import org.bson.types.ObjectId;

@Entity
public class MonitorData {
	@Id
	private String id;
	private Date collectTime;
	private String deviceId;
	private String deviceName;
	private double reading1;
	@Enumerated(EnumType.STRING)
	@Column(name="reading1_type")
	private ReadingType reading1Type;
	private double reading2;
	@Enumerated(EnumType.STRING)
	@Column(name="reading2_type")
	private ReadingType reading2Type;
	private Date addedTime = new Date();

	public MonitorData(Date collectTime, String deviceId, String deviceName,
			double reading1, ReadingType reading1Type, double reading2,
			ReadingType reading2Type) {
		super();
		this.id = new ObjectId().toHexString();
		this.collectTime = collectTime;
		this.deviceId = deviceId;
		this.deviceName = deviceName;
		this.reading1 = reading1;
		this.reading1Type = reading1Type;
		this.reading2 = reading2;
		this.reading2Type = reading2Type;
	}

	public MonitorData() {
		super();
	}

	@Override
	public String toString() {
		return "MonitorData [id=" + id + ", collectTime=" + collectTime
				+ ", deviceId=" + deviceId + ", deviceName=" + deviceName
				+ ", reading1=" + reading1 + ", reading1Type=" + reading1Type
				+ ", reading2=" + reading2 + ", reading2Type=" + reading2Type
				+ ", addedTime=" + addedTime + "]";
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Date getCollectTime() {
		return collectTime;
	}

	public void setCollectTime(Date collectTime) {
		this.collectTime = collectTime;
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

	public double getReading1() {
		return reading1;
	}

	public void setReading1(double reading1) {
		this.reading1 = reading1;
	}

	public ReadingType getReading1Type() {
		return reading1Type;
	}

	public void setReading1Type(ReadingType reading1Type) {
		this.reading1Type = reading1Type;
	}

	public double getReading2() {
		return reading2;
	}

	public void setReading2(double reading2) {
		this.reading2 = reading2;
	}

	public ReadingType getReading2Type() {
		return reading2Type;
	}

	public void setReading2Type(ReadingType reading2Type) {
		this.reading2Type = reading2Type;
	}

	public Date getAddedTime() {
		return addedTime;
	}

	public void setAddedTime(Date addedTime) {
		this.addedTime = addedTime;
	}

}
