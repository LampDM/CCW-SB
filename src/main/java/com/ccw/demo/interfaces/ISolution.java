package com.ccw.demo.interfaces;

import java.util.List;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.Solution;
import com.ccw.demo.model.Task;
import com.ccw.demo.model.User;

@Repository
public interface ISolution extends CrudRepository<Solution, Integer> {
	
	List<Solution> findByUsrAndTsk(User usr, Task tsk);
	
}

