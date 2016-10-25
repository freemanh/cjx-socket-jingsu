package com.cjx.monitor.jingsu;

import static org.junit.Assert.*;

import org.junit.Test;

import com.cjx.monitor.jingsu.domain.ReadingType;
import com.cjx.monitor.jingsu.domain.Sensor;

public class SensorTest {
	@Test
	public void test() {
		Sensor s = new Sensor(10.0, 20.0, 5.0, 0.0, ReadingType.TEMP);
		assertTrue(s.isNormal());
		
		s.setReading(4.9);
		assertFalse(s.isNormal());
		
		s.setReading(20.1);
		assertFalse(s.isNormal());
	}
}
