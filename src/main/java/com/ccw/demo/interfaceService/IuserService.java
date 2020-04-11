package com.ccw.demo.interfaceService;


import com.ccw.demo.model.User;


public interface IuserService {
	
	public boolean existsUser(String username);

	public int save(User u);

	User getUser(String username);

}
