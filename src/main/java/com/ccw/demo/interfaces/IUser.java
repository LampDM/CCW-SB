package com.ccw.demo.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import com.ccw.demo.model.User;

@Repository
public interface IUser extends CrudRepository<User, Integer> {

	@Query(value = "select * from users where username = ?1", nativeQuery = true)
	List<User> findByName(String namex);
}
