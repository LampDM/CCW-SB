package com.ccw.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.ccw.demo.interfaceService.IuserService;
import com.ccw.demo.interfaces.IUser;
import com.ccw.demo.model.User;

@Service
public class UserService implements IuserService {

	@Autowired
	private IUser data;
	
	@Override
	public List<User> list() {
		return (List<User>)data.findAll();
	}

	@Override
	public Optional<User> listId(int id) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int save(User u) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void delete(int id) {
		// TODO Auto-generated method stub
		
	}

}
