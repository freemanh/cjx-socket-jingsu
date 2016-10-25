package com.cjx.monitor.jingsu.service;

import javax.transaction.Transactional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.cjx.monitor.jingsu.codec.MonitorMessage;
import com.cjx.monitor.jingsu.domain.Alarm;
import com.cjx.monitor.jingsu.domain.Device;
import com.cjx.monitor.jingsu.domain.MonitorData;
import com.cjx.monitor.jingsu.repo.AlarmRepo;
import com.cjx.monitor.jingsu.repo.DeviceRepo;
import com.cjx.monitor.jingsu.repo.MonitorDataRepo;

@Service
public class MonitorDataPostProcessor {
	@SuppressWarnings("unused")
	private final static Logger LOG = LoggerFactory
			.getLogger(MonitorDataPostProcessor.class);

	@Autowired
	private DeviceRepo deviceRepo;
	@Autowired
	private MonitorDataRepo monitorDataRepo;
	@Autowired
	private AlarmRepo alarmRepo;
	@Autowired
	private SmsSender sms;

	@Transactional
	public void exec(MonitorMessage msg) {
		Device device = deviceRepo.findOneByCode(msg.getDeviceCode());

		MonitorData data = new MonitorData(msg.getDate(), device.getId(),
				device.getName(), msg.getReading1(), device.getSensor1()
						.getType(), msg.getReading2(), device.getSensor2()
						.getType());
		monitorDataRepo.save(data);

		boolean senson1Before = device.getSensor1().isNormal();
		boolean sensor2Before = device.getSensor2().isNormal();

		device.updateReadings(msg.getReading1(), msg.getReading2(),
				msg.getDate());

		boolean sensor1After = device.getSensor1().isNormal();
		boolean sensor2After = device.getSensor2().isNormal();

		if (!sensor1After && senson1Before != sensor1After) {
			// alarm
			Alarm alarm = new Alarm(device.getId(), device.getName(),
					String.format("设备：%1s读数异常，正常范围为：%2s-%3s，实际为：%4s",
							device.getName(), device.getSensor1().getMin(),
							device.getSensor1().getMax(), msg.getReading1()));
			alarmRepo.save(alarm);
			sms.send(alarm.getContent());
		}
		if (!sensor2After && sensor2Before != sensor2After) {
			// alarm
			Alarm alarm = new Alarm(device.getId(), device.getName(),
					String.format("设备：%1s读数异常，正常范围为：%2s-%3s，实际为：%4s",
							device.getName(), device.getSensor2().getMin(),
							device.getSensor2().getMax(), msg.getReading2()));
			alarmRepo.save(alarm);
			sms.send(alarm.getContent());
		}

		if (device.isSupportPowerOff()) {
			if (msg.isPoweroff() && !device.isPowerOff()) {
				// alarm
				Alarm alarm = new Alarm(device.getId(), device.getName(),
						String.format("设备：%1s发生断电报警！", device.getName()));

				alarmRepo.save(alarm);
				sms.send(alarm.getContent());
			}
			device.setPowerOff(msg.isPoweroff());
		}

		deviceRepo.save(device);
	}

	public void setDeviceRepo(DeviceRepo deviceRepo) {
		this.deviceRepo = deviceRepo;
	}

	public void setMonitorDataRepo(MonitorDataRepo monitorDataRepo) {
		this.monitorDataRepo = monitorDataRepo;
	}

	public void setAlarmRepo(AlarmRepo alarmRepo) {
		this.alarmRepo = alarmRepo;
	}

	public void setSms(SmsSender sms) {
		this.sms = sms;
	}

}
