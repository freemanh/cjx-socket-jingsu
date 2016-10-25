package com.cjx.monitor.jingsu.repo;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import com.cjx.monitor.jingsu.domain.User;

public interface UserRepo extends CrudRepository<User, String> {
	@Query(value = "select mobile from User")
	public List<String> findMobiles();
}
