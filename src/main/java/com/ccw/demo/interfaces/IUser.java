package com.ccw.demo.interfaces;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.User;

@Repository
public interface IUser extends CrudRepository<User, Integer> {

	List<User> findByUsername(String username);
	
}
