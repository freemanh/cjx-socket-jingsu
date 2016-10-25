package com.cjx.monitor.jingsu.codec;

import java.io.Serializable;
import java.util.Date;

public class MonitorMessage implements Serializable {
	private static final long serialVersionUID = -8463977024952077993L;
	private String deviceCode;
	private Double reading1;
	private Double reading2;
	private boolean poweroff;
	private int failCount;
	private Date date;

	public MonitorMessage(String deviceCode, Double reading1, Double reading2, boolean poweroff, int failCount, Date date) {
		super();
		this.deviceCode = deviceCode;
		this.reading1 = reading1;
		this.reading2 = reading2;
		this.poweroff = poweroff;
		this.failCount = failCount;
		this.date = date;
	}

	@Override
	public String toString() {
		return "MonitorMessage [deviceCode=" + deviceCode + ", reading1=" + reading1 + ", reading2=" + reading2 + ", poweroff=" + poweroff + ", failCount=" + failCount + ", date="
				+ date + "]";
	}

	public Double getReading1() {
		return reading1;
	}

	public void setReading1(Double reading1) {
		this.reading1 = reading1;
	}

	public Double getReading2() {
		return reading2;
	}

	public void setReading2(Double reading2) {
		this.reading2 = reading2;
	}

	public boolean isPoweroff() {
		return poweroff;
	}

	public void setPoweroff(boolean poweroff) {
		this.poweroff = poweroff;
	}

	public int getFailCount() {
		return failCount;
	}

	public Date getDate() {
		return date;
	}

	public String getDeviceCode() {
		return deviceCode;
	}

	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}

	public void setFailCount(int failCount) {
		this.failCount = failCount;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
