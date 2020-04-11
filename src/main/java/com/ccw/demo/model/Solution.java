package com.ccw.demo.model;


import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

@Entity
@Table(name = "solutions", uniqueConstraints = {
		@UniqueConstraint(name = "unq_valuation_4", columnNames = { "tsk_id", "usr_id" }) }

)

public class Solution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private Task tsk;
	
	@ManyToOne
	private User usr;
	
	private String answer;

	private String score;

	
	public User getUsr() {
		return usr;
	}

	public void setUsr(User usr) {
		this.usr = usr;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public Task getTsk() {
		return tsk;
	}

	public void setTsk(Task tsk) {
		this.tsk = tsk;
	}

	public int getSolution_id() {
		return id;
	}

	public void setSolution_id(int solution_id) {
		this.id = solution_id;
	}

	public String getAnswer() {
		return answer;
	}

	public void setAnswer(String answer) {
		this.answer = answer;
	}

	public String getScore() {
		return score;
	}

	public void setScore(String score) {
		this.score = score;
	}

}
