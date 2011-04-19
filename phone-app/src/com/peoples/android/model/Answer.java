package com.peoples.android.model;


/**
 * 
 * CREATE TABLE answers (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	question_id INT UNSIGNED NOT NULL,
	subject_id INT UNSIGNED NOT NULL,
	choice_id INT UNSIGNED,
	ans_text TEXT,
	created DATETIME);
 * 
 * 
 * @author Diego
 *
 */
public class Answer {
	
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int ANSWER_KEY;
	
	//question_id INT UNSIGNED NOT NULL,
	private Question question;
	
	//subject_id INT UNSIGNED NOT NULL,
	private Subject subject;
	
	//choice_id INT UNSIGNED,
	private Choice choice;
	
	//ans_text TEXT,
	private String text;
	private int index;
	
	public String getText() {
		return text;
	}
	
	//created DATETIME);
	private String datetime;
	
	private int type; // 0 if multiple choice, 1 if free response
	
	public Answer (String a) {
		type = 1;
		text = a;
	}
	
	public Answer (int a) {
		type = 0;
		index = a;
		text = "" + index;
	}
	
	public int getIndex() {
		return index;
	}


}
