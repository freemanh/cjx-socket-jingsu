package com.cjx.monitor.jingsu;

import java.util.Date;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.cjx.monitor.jingsu.domain.Alarm;
import com.cjx.monitor.jingsu.domain.Device;
import com.cjx.monitor.jingsu.domain.MonitorData;
import com.cjx.monitor.jingsu.domain.ReadingType;
import com.cjx.monitor.jingsu.domain.Sensor;
import com.cjx.monitor.jingsu.domain.User;
import com.cjx.monitor.jingsu.repo.AlarmRepo;
import com.cjx.monitor.jingsu.repo.DeviceRepo;
import com.cjx.monitor.jingsu.repo.MonitorDataRepo;
import com.cjx.monitor.jingsu.repo.UserRepo;

@RunWith(SpringRunner.class)
@SpringBootTest
public class BaseIT {
	@Autowired
	private AlarmRepo alarmRepo;

	@Autowired
	private MonitorDataRepo monitorDataRepo;
	@Autowired
	private DeviceRepo deviceRepo;
	
	@Autowired
	private UserRepo userRepo;

	@Test
	@Ignore
	public void testAlarmRepo() {
		alarmRepo.save(new Alarm("deviceId", "测试设备", "over temp"));
	}

	@Test
	@Ignore
	public void testMonitorDataRepo() {
		monitorDataRepo.save(new MonitorData(new Date(), "deviceId",
				"device name", 10.0, ReadingType.TEMP, 50.0, ReadingType.HUM));
	}

	@Test
	@Ignore
	public void testDeviceRepo() {
		Device device = new Device("name", "code", new Sensor(0.00, 10.0, 5.0,
				0.0, ReadingType.TEMP), new Sensor(0.00, 50.0, 10.0,
				0.0, ReadingType.HUM));
		deviceRepo.save(device);
	}
	
	@Test
	@Ignore
	public void testUserRepo(){
		User user = new User("test", "123", "power1128", "15308039727");
		userRepo.save(user);
	}
	
	@Test
	public void testFindMobile(){
		System.out.println(userRepo.findMobiles());
	}
}
