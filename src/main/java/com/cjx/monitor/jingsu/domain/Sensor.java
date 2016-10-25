package com.cjx.monitor.jingsu.domain;

import javax.persistence.Embeddable;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

@Embeddable
public class Sensor {
	private double reading;
	private double max;
	private double min;
	private double revision;
	@Enumerated(EnumType.STRING)
	private ReadingType type;

	public Sensor(double reading, double max, double min, double revision,
			ReadingType type) {
		super();
		this.reading = reading;
		this.max = max;
		this.min = min;
		this.revision = revision;
		this.type = type;
	}

	public Sensor() {
		super();
	}

	public boolean isNormal() {
		return reading >= min && reading <= max;
	}

	@Override
	public String toString() {
		return "Sensor [reading=" + reading + ", max=" + max + ", min=" + min
				+ ", revision=" + revision + ", type=" + type + "]";
	}

	public double getReading() {
		return reading;
	}

	public void setReading(double reading) {
		this.reading = reading;
	}

	public double getMax() {
		return max;
	}

	public void setMax(double max) {
		this.max = max;
	}

	public double getMin() {
		return min;
	}

	public void setMin(double min) {
		this.min = min;
	}

	public double getRevision() {
		return revision;
	}

	public void setRevision(double revision) {
		this.revision = revision;
	}

	public ReadingType getType() {
		return type;
	}

	public void setType(ReadingType type) {
		this.type = type;
	}

}
