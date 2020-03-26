package com.ccw.demo.interfaces;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.Task;

@Repository
public interface ITask extends CrudRepository<Task, Integer> {

}
