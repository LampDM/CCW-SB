package com.ccw.demo.interfaces;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.Solution;

@Repository
public interface ISolution extends CrudRepository<Solution, Integer> {

}

