package com.ccw.demo.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "solutions")
public class Solution {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@ManyToOne
	private Task tsk;
	
	@ManyToOne
	private User usr;
	
	@Column(columnDefinition="TEXT")
	private String answer;

	private String score;
	
	@Column(columnDefinition="TEXT")
	private String info;
	
	private String date;

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

	public String getInfo() {
		return info;
	}

	public void setInfo(String info) {
		this.info = info;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}
	
}
