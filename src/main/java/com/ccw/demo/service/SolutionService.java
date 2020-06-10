package com.ccw.demo.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ccw.demo.interfaceService.IsolutionService;
import com.ccw.demo.interfaces.ISolution;
import com.ccw.demo.model.Solution;
import com.ccw.demo.model.Task;
import com.ccw.demo.model.User;

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

	public Solution getSolution(User usr, Task tsk) {
		List<Solution> ls = data.findByUsrAndTsk(usr, tsk);
		if (ls.size() == 0) {
			Solution s = new Solution();
			s.setUsr(usr);
			s.setTsk(tsk);
			return s;
		}
		return ls.get(0);
	}

	@Override
	public void delete(int id) {
		data.deleteById(id);
	}

	@Override
	public void delete(Solution solution) {
		data.delete(solution);
	}

	public List<Solution> getSolutions(User usr, Task tsk) {
		List<Solution> ls = data.findByUsrAndTsk(usr, tsk);
		if (ls.size() == 0) {
			Solution s = new Solution();
			s.setUsr(usr);
			s.setTsk(tsk);
			ls.add(s);
		}
		return ls;
	}

}
