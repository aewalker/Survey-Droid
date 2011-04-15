package com.peoples.model;


/**
 * CREATE TABLE choices (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	text VARCHAR(255),
	question_id INT UNSIGNED);
 * 
 * 
 * @author diego
 *
 */
public class Choice {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int CHOICE_KEY;
	
	//text VARCHAR(255),
	private String choice_text;
	
	//question_id INT UNSIGNED);
	private Question question;

}
