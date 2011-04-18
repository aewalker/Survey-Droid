package src.com.peoples.model;


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
	private int prev_q;

	//next_q INT UNSIGNED NOT NULL);
	private int next_q;
	
}
