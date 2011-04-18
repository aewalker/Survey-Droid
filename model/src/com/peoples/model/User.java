/**
 * 
 */
package src.com.peoples.model;

/**
 * @author Diego
 * 
 * Following the database creator
 *
 */
public class User {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int id;
	
	//username VARCHAR(20) NOT NULL UNIQUE,
	private String username;
	
	//email VARCHAR(320) NOT NULL UNIQUE,
	private String email;
	
	//password CHAR(41) NOT NULL,
	private String password;
	
	//first_name VARCHAR(255),
	private String first_name;
	
	//last_name VARCHAR(255),
	private String last_name;
	
	//admin TINYINT(1) DEFAULT 0);
	private boolean admin;
}
