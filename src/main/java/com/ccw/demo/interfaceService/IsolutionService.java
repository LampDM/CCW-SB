package com.ccw.demo.interfaceService;

import java.util.List;
import java.util.Optional;
import com.ccw.demo.model.Solution;

public interface IsolutionService {
	public List<Solution> list();

	public Optional<Solution> listId(int id);

	public int save(Solution s);

	public void delete(int id);
}
