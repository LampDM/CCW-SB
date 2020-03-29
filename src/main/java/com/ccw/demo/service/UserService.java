package com.ccw.demo.service;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccw.demo.interfaceService.IuserService;
import com.ccw.demo.interfaces.IUser;
import com.ccw.demo.model.User;

@Component
public class UserService implements IuserService {

	@Autowired
	private IUser data;
	
	@Override
	public boolean existsUser(String username) {
		return !(data.findByName(username).isEmpty());
	}
	

	@Override
	public int save(User u) {
		u.setRoles("ROLE_USER");
		
		Short en = 1;
		u.setEnabled(en);
		
		data.save(u);
		return 0;
	}

}
