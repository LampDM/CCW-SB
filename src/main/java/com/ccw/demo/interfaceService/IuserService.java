package com.ccw.demo.interfaceService;

import java.util.List;
import java.util.Optional;

import com.ccw.demo.model.User;

public interface IuserService {
	public List<User> list();

	public Optional<User> listId(int id);

	public int save(User u);

	public void delete(int id);
}
