package com.ccw.demo.interfaces;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.User;

@Repository
public interface IUser extends CrudRepository<User, Integer> {

}
