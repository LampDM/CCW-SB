package com.ccw.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.ccw.demo.interfaceService.IuserService;
import com.ccw.demo.interfaces.IUser;
import com.ccw.demo.model.User;

@Component
public class UserService implements IuserService {

	@Autowired
	private IUser data;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Override
	public boolean existsUser(String username) {
		return !(data.findByName(username).isEmpty());
	}
	
	@Override
	public int save(User u) {
		u.setPassword(passwordEncoder.encode(u.getPassword()));
		data.save(u);
		return 0;
	}

	@Override
	public int getId(String username) {
		return data.findByName(username).get(0).getId();
	}
	
	

}
