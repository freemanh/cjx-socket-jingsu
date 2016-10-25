package com.cjx.monitor.jingsu;

import java.util.Date;

import org.bson.types.ObjectId;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

import com.cjx.monitor.jingsu.codec.MonitorMessage;
import com.cjx.monitor.jingsu.domain.Alarm;
import com.cjx.monitor.jingsu.domain.Device;
import com.cjx.monitor.jingsu.domain.MonitorData;
import com.cjx.monitor.jingsu.domain.ReadingType;
import com.cjx.monitor.jingsu.domain.Sensor;
import com.cjx.monitor.jingsu.repo.AlarmRepo;
import com.cjx.monitor.jingsu.repo.DeviceRepo;
import com.cjx.monitor.jingsu.repo.MonitorDataRepo;
import com.cjx.monitor.jingsu.service.MonitorDataPostProcessor;
import com.cjx.monitor.jingsu.service.SmsSender;

@RunWith(MockitoJUnitRunner.class)
public class MonitorDataPostProcessorTest {
	@Mock
	private DeviceRepo deviceRepo;
	@Mock
	private MonitorDataRepo monitorDataRepo;
	@Mock
	private AlarmRepo alarmRepo;
	@Mock
	private SmsSender sms;

	private MonitorDataPostProcessor p;

	@Before
	public void setup() {
		p = new MonitorDataPostProcessor();
		p.setDeviceRepo(deviceRepo);
		p.setMonitorDataRepo(monitorDataRepo);
		p.setAlarmRepo(alarmRepo);
		p.setSms(sms);
	}

	@Test
	public void testFirstData() {
		// given
		String deviceCode = "1001";

		Device device = new Device("测试设备", deviceCode, new Sensor(11.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		device.setId(new ObjectId().toHexString());

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		p.exec(new MonitorMessage(deviceCode, 10.0, 30.0, false, 0, new Date()));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo, never()).save(any(Alarm.class));
		verify(sms, never()).send(anyString());
	}

	@Test
	public void testNormalData() throws InterruptedException {
		// given
		String deviceCode = "1001";

		Date lastReadingTime = new Date();
		Thread.sleep(1000);
		Date newDataTime = new Date();

		Device device = new Device("测试设备", deviceCode, new Sensor(11.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		device.setId(new ObjectId().toHexString());
		device.setLastReadingTime(lastReadingTime);

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		p.exec(new MonitorMessage(deviceCode, 10.0, 30.0, false, 0, newDataTime));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo, never()).save(any(Alarm.class));
		verify(sms, never()).send(anyString());

		assertTrue(device.getLastReadingTime().equals(newDataTime));
		assertEquals(device.getSensor1().getReading(), 10.0, 0.01);
	}

	@Test
	public void testLegacyData() throws InterruptedException {
		// given
		String deviceCode = "1001";

		Date newDataTime = new Date();
		Thread.sleep(1000);
		Date lastReadingTime = new Date();

		Device device = new Device("测试设备", deviceCode, new Sensor(11.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		device.setId(new ObjectId().toHexString());
		device.setLastReadingTime(lastReadingTime);

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		p.exec(new MonitorMessage(deviceCode, 10.0, 30.0, false, 0, newDataTime));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo, never()).save(any(Alarm.class));
		verify(sms, never()).send(anyString());

		assertFalse(device.getLastReadingTime().equals(newDataTime));
		assertEquals(device.getSensor1().getReading(), 11.0, 0.01);
	}

	@Test
	public void testReadingOverlimit() throws InterruptedException {
		// given
		String deviceCode = "1001";

		Date lastReadingTime = new Date();
		Thread.sleep(1000);
		Date newDataTime = new Date();

		Device device = new Device("测试设备", deviceCode, new Sensor(11.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		device.setId(new ObjectId().toHexString());
		device.setLastReadingTime(lastReadingTime);

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		// 21.0 is over the max of reading1
		p.exec(new MonitorMessage(deviceCode, 21.0, 30.0, false, 0, newDataTime));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo).save(any(Alarm.class));
		verify(sms).send(anyString());

		assertTrue(device.getLastReadingTime().equals(newDataTime));
		assertEquals(device.getSensor1().getReading(), 21.0, 0.01);
	}

	@Test
	public void testReadingBackToNormal() throws InterruptedException {
		// given
		String deviceCode = "1001";

		Date lastReadingTime = new Date();
		Thread.sleep(1000);
		Date newDataTime = new Date();

		// 21.0 is over the max of reading 1
		Device device = new Device("测试设备", deviceCode, new Sensor(21.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		device.setId(new ObjectId().toHexString());
		device.setLastReadingTime(lastReadingTime);

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		// 21.0 is over the max of reading1
		p.exec(new MonitorMessage(deviceCode, 11.0, 30.0, false, 0, newDataTime));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo, never()).save(any(Alarm.class));
		verify(sms, never()).send(anyString());

		assertTrue(device.getLastReadingTime().equals(newDataTime));
		assertEquals(device.getSensor1().getReading(), 11.0, 0.01);
	}

	@Test
	public void testPowerOff() throws InterruptedException {
		// given
		String deviceCode = "1001";

		Date lastReadingTime = new Date();
		Thread.sleep(1000);
		Date newDataTime = new Date();

		Device device = new Device("测试设备", deviceCode, new Sensor(11.0, 20.0,
				5.0, 0.0, ReadingType.TEMP), new Sensor(29.0, 50.0, 20.0, 0.0,
				ReadingType.HUM));
		// given the device is power on and support power off alarm
		device.setSupportPowerOff(true);
		device.setPowerOff(false);
		device.setId(new ObjectId().toHexString());
		device.setLastReadingTime(lastReadingTime);

		when(deviceRepo.findOneByCode(deviceCode)).thenReturn(device);

		// when
		// 21.0 is over the max of reading1, and power off is true
		p.exec(new MonitorMessage(deviceCode, 21.0, 30.0, true, 0, newDataTime));

		// then
		verify(monitorDataRepo).save(any(MonitorData.class));
		verify(deviceRepo).save(device);
		verify(alarmRepo, times(2)).save(any(Alarm.class));
		verify(sms, times(2)).send(anyString());

		assertTrue(device.getLastReadingTime().equals(newDataTime));
		assertEquals(device.getSensor1().getReading(), 21.0, 0.01);
	}
}
