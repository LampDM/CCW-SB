package com.ccw.demo.service;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccw.demo.interfaceService.IsolutionService;
import com.ccw.demo.interfaces.ISolution;
import com.ccw.demo.model.Solution;

@Component
public class SolutionService implements IsolutionService {

	@Autowired
	private ISolution data;

	@Override
	public List<Solution> list() {
		return (List<Solution>) data.findAll();
	}

	@Override
	public Optional<Solution> listId(int id) {
		return data.findById(id);
	}

	@Override
	public int save(Solution s) {
		Solution sl = data.save(s);
		if (sl.equals(null)) {
			return 1;
		}
		return 0;
	}

	@Override
	public void delete(int id) {
		data.deleteById(id);

	}

}

