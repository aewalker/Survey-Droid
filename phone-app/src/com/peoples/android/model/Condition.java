package com.peoples.android.model;


/**
 * CREATE TABLE conditions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	branch_id INT UNSIGNED NOT NULL,
	question_id INT UNSIGNED NOT NULL,
	choice_id INT UNSIGNED NOT NULL,
	type TINYINT UNSIGNED NOT NULL);
			
			
 * @author diego
 *
 */
public class Condition {
	
	private static final int JUST_WAS = 0;
	private static final int EVER_WAS = 1;
	private static final int HAS_NEVER_BEEN = 2;
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int CONDITION_KEY;
	
	//branch_id INT UNSIGNED NOT NULL,
	private Branch branch;
	
	//prereq_q INT UNSIGNED NOT NULL,
	private Question question;
	
	//req_choice INT UNSIGNED NOT NULL);
	private Choice choice;
	
	//"just was", "ever was", or "has never been"
	private int type;
		
}
