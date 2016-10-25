package com.cjx.monitor.jingsu.domain;

import java.util.Date;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.Id;

import org.bson.types.ObjectId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Entity
public class Device {
	private static final Logger LOG = LoggerFactory.getLogger(Device.class);

	@Id
	private String id;
	private String name;
	private String code;
	private boolean powerOff = false;
	private boolean supportPowerOff = false;
	@Embedded
	@AttributeOverrides(value = {
			@AttributeOverride(column = @Column(name = "reading1"), name = "reading"),
			@AttributeOverride(column = @Column(name = "max1"), name = "max"),
			@AttributeOverride(column = @Column(name = "min1"), name = "min"),
			@AttributeOverride(column = @Column(name = "revision1"), name = "revision"),
			@AttributeOverride(column = @Column(name = "reading1_type"), name = "type") })
	private Sensor sensor1;
	@Embedded
	@AttributeOverrides(value = {
			@AttributeOverride(column = @Column(name = "reading2"), name = "reading"),
			@AttributeOverride(column = @Column(name = "max2"), name = "max"),
			@AttributeOverride(column = @Column(name = "min2"), name = "min"),
			@AttributeOverride(column = @Column(name = "revision2"), name = "revision"),
			@AttributeOverride(column = @Column(name = "reading2_type"), name = "type") })
	private Sensor sensor2;
	private Date addedTime = new Date();
	private Date changedTime;
	private Date lastReadingTime;

	public Device(String name, String code, Sensor sensor1, Sensor sensor2) {
		super();
		this.id = new ObjectId().toHexString();
		this.name = name;
		this.code = code;
		this.sensor1 = sensor1;
		this.sensor2 = sensor2;
		this.addedTime = new Date();
		this.changedTime = this.addedTime;
	}

	public Device() {
		super();
	}

	public void updateReadings(Double reading1, Double reading2,
			Date newLastDate) {
		if (null == this.lastReadingTime || this.lastReadingTime.before(newLastDate)) {
			this.sensor1.setReading(reading1);
			this.sensor2.setReading(reading2);
			this.lastReadingTime = newLastDate;
		} else {
			LOG.info("Not update device current reading as this is a legacy data");
		}
	}

	@Override
	public String toString() {
		return "Device [id=" + id + ", name=" + name + ", code=" + code
				+ ", powerOff=" + powerOff + ", supportPowerOff="
				+ supportPowerOff + ", sensor1=" + sensor1 + ", sensor2="
				+ sensor2 + ", addedTime=" + addedTime + ", changedTime="
				+ changedTime + "]";
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

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public boolean isPowerOff() {
		return powerOff;
	}

	public void setPowerOff(boolean powerOff) {
		this.powerOff = powerOff;
	}

	public boolean isSupportPowerOff() {
		return supportPowerOff;
	}

	public void setSupportPowerOff(boolean supportPowerOff) {
		this.supportPowerOff = supportPowerOff;
	}

	public Sensor getSensor1() {
		return sensor1;
	}

	public void setSensor1(Sensor sensor1) {
		this.sensor1 = sensor1;
	}

	public Sensor getSensor2() {
		return sensor2;
	}

	public void setSensor2(Sensor sensor2) {
		this.sensor2 = sensor2;
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

	public Date getLastReadingTime() {
		return lastReadingTime;
	}

	public void setLastReadingTime(Date lastReadingTime) {
		this.lastReadingTime = lastReadingTime;
	}

}
