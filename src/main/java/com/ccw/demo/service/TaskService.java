package com.ccw.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.ccw.demo.interfaceService.ItaskService;
import com.ccw.demo.interfaces.ITask;
import com.ccw.demo.model.Task;

@Component
public class TaskService implements ItaskService {

	@Autowired
	private ITask data;

	@Override
	public List<Task> list() {
		return (List<Task>) data.findAll();
	}

	@Override
	public Optional<Task> listId(int id) {
		return data.findById(id);
	}

	@Override
	public int save(Task t) {
		Task tk = data.save(t);
		if (tk.equals(null)) {
			return 1;
		}
		return 0;
	}

	@Override
	public void delete(int id) {
		data.deleteById(id);

	}

}
