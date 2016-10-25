package com.cjx.monitor.jingsu.repo;

import org.springframework.data.repository.CrudRepository;

import com.cjx.monitor.jingsu.domain.Alarm;

public interface AlarmRepo extends CrudRepository<Alarm, String> {

}
