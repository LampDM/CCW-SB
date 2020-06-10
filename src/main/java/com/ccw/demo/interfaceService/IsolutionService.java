package com.ccw.demo.interfaceService;

import java.util.List;
import java.util.Optional;
import com.ccw.demo.model.Solution;
import com.ccw.demo.model.Task;
import com.ccw.demo.model.User;

public interface IsolutionService {
	public List<Solution> list();

	public Optional<Solution> listId(int id);

	public int save(Solution s);

	public void delete(int id);
	
	public Solution getSolution(User usr, Task tsk);

	public void delete(Solution solution);

	public List<Solution> getSolutions(User usr, Task tsk);

}
