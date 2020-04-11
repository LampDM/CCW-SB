package com.ccw.demo.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "tasks")
public class Task {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String name;
	private String tests;
	private String description;
	private String user_code;

	//One task will have many solutions
	@OneToMany(mappedBy = "tsk")
	private Set<Solution> sols;
	
	public Set<Solution> getSols() {
		return sols;
	}

	public void setSols(Set<Solution> sols) {
		this.sols = sols;
	}

	public Task() {

	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getTests() {
		return tests;
	}

	public void setTests(String tests) {
		this.tests = tests;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getUser_code() {
		return user_code;
	}

	public void setUser_code(String uc) {
		this.user_code = uc;
	}

}
