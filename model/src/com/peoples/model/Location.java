package com.peoples.model;


/**
 * CREATE TABLE locations (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	longitude DOUBLE NOT NULL,
	lattitude DOUBLE NOT NULL);
	
 * @author Diego
 *
 */
public class Location {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int LOCATION_KEY;
	
	//subject_id INT UNSIGNED NOT NULL,
	private Subject subject;
	
	//created DATETIME NOT NULL,
	private int datetime;
	
	//longitude DOUBLE NOT NULL,
	private double longitude;
	
	//latitude DOUBLE NOT NULL);
	private double latitude;
	

}
