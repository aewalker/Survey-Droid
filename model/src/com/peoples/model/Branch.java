package com.peoples.model;


/**
 * CREATE TABLE branches (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	prev_q INT UNSIGNED NOT NULL, //foreign keys//
	next_q INT UNSIGNED NOT NULL) 
 * 
 * @author Diego
 *
 **/
public class Branch {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int BRANCH_KEY;

	//prev_q INT UNSIGNED NOT NULL, /*foreign keys*/
	private Question prev_q;

	//next_q INT UNSIGNED NOT NULL);
	private Question next_q;
	
}
