package com.peoples.model;

/**
 * CREATE TABLE status_changes (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	status ENUM('enabled', 'disbled') NOT NULL,
	feature ENUM('gps', 'call log', 'text log', 'app') NOT NULL);
 * @author diego
 *
 */
public class StatusChange {
	
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int STATUSCHANGE_KEY;
	
	//subject_id INT UNSIGNED NOT NULL,
	private int subject_id;
	
	//created DATETIME NOT NULL,
	private String datetime;
	
	//status ENUM('enabled', 'disbled') NOT NULL,
	//TODO: implement 
	//feature ENUM('gps', 'call log', 'text log', 'app') NOT NULL);

}
