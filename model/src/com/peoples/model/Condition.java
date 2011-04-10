package com.peoples.model;


/**
 * CREATE TABLE conditions (
			id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
			branch_id INT UNSIGNED NOT NULL,
			prereq_q INT UNSIGNED NOT NULL,
			req_choice INT UNSIGNED NOT NULL);
			
			
 * @author diego
 *
 */
public class Condition {
	
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int CONDITION_KEY;
	
	//branch_id INT UNSIGNED NOT NULL,
	private int branch_id;
	
	//prereq_q INT UNSIGNED NOT NULL,
	private int prereq_q;
	
	//req_choice INT UNSIGNED NOT NULL);
	private int req_choice;
		
}
