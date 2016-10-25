package com.cjx.monitor.jingsu.repo;

import org.springframework.data.repository.CrudRepository;

import com.cjx.monitor.jingsu.domain.Device;

public interface DeviceRepo extends CrudRepository<Device, String> {
	public Device findOneByCode(String code);
}
