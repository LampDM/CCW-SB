package com.ccw.demo.interfaceService;

import java.util.List;
import java.util.Optional;

import com.ccw.demo.model.Task;

public interface ItaskService {
	public List<Task> list();

	public Optional<Task> listId(int id);

	public int save(Task t);

	public void delete(int id);
}
