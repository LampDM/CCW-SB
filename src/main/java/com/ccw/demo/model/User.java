package com.ccw.demo.model;

import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;

@Entity
@Table(name = "users")
public class User {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	private String username;
	private short Enabled = 1;
	private String Password;
	private String Roles = "ROLE_USER";
	private String Scores;
	
	//One user will have many solutions
	@OneToMany(mappedBy = "usr")
	private Set<Solution> sols;
	
	public Set<Solution> getSols() {
		return sols;
	}

	public void setSols(Set<Solution> sols) {
		this.sols = sols;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public short getEnabled() {
		return Enabled;
	}

	public void setEnabled(short enabled) {
		Enabled = enabled;
	}

	public String getPassword() {
		return Password;
	}

	public void setPassword(String password) {
		Password = password;
	}

	public String getRoles() {
		return Roles;
	}

	public void setRoles(String roles) {
		Roles = roles;
	}

	public String getScores() {
		return Scores;
	}

	public void setScores(String scores) {
		Scores = scores;
	}

}
