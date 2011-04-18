package src.com.peoples.model;





/**
 *
 * CCREATE TABLE calls (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	contact_id VARCHAR(255) NOT NULL,
	created DATETIME NOT NULL,
	type TINYINT NOT NULL,
	duration SMALLINT UNSIGNED);
 * 
 * 
 * @author diego
 *
 */
public class Call {
	
	private static final int OUTGOING_CALL = 0;
	private static final int INCOMING_CALL = 1;
	private static final int OUTGOING_TEXT = 2;
	private static final int INCOMING_TEXT = 3;
	private static final int MISSED_CALL = 4;
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int CALLACTION_KEY;
	
	//subject_id INT UNSIGNED NOT NULL,
	private Subject subject;
	
	//contact_id VARCHAR(255) NOT NULL,
	private String contact_id; //essentially a UUID
	
	//created DATETIME NOT NULL,
	private String creationTime;
	
	//outgoing call, incoming call, outoing text, incoming text, or missed call
	private int tpye;
	
	//duration SMALLINT UNSIGNED
	private int duration; //in seconds
	
}
