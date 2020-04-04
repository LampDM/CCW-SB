package com.ccw.demo.model;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

@Entity
@Table(name = "solutions")
public class Solution {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;
	
// TODO fix the foreign key annotation mess
//	@ManyToOne
//	@JoinColumn(name = "User_Id")
	private int user_id;
	
//	@ManyToOne
//	@JoinColumn(name = "Task_Id")
	private int task_id;
	
	private String answer;
	
	private String score;

	public int getSolution_id() {
		return id;
	}

	public void setSolution_id(int solution_id) {
		this.id = solution_id;
	}

	public int getUsr_id() {
		return user_id;
	}

	public void setUsr_id(int usr_id) {
		this.user_id = usr_id;
	}

	public int getTask_id() {
		return task_id;
	}

	public void setTask_id(int task_id) {
		this.task_id = task_id;
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
