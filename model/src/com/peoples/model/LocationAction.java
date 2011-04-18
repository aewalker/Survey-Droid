package src.com.peoples.model;


/**
 * CREATE TABLE location_actions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	longitude DOUBLE NOT NULL,
	lattitude DOUBLE NOT NULL);
	
 * @author Diego
 *
 */
public class LocationAction {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int LOCATIONACTION_KEY;
	
	//subject_id INT UNSIGNED NOT NULL,
	private int subject_id;
	
	//created DATETIME NOT NULL,
	private int datetime;
	
	//longitude DOUBLE NOT NULL,
	private double longitude;
	
	//lattitude DOUBLE NOT NULL);
	//TODO: misspelled
	private double lattitude;
	

}
