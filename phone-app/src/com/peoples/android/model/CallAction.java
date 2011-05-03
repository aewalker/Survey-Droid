/*---------------------------------------------------------------------------*
 * CallAction.java                                                           *
 *                                                                           *
 * Currently unused; left in for future work.                                *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

/**
 *
 * CREATE TABLE call_actions (
		id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
		subject_id INT UNSIGNED NOT NULL,
		contact_id INT UNSIGNED NOT NULL,
		created DATETIME NOT NULL,
		act_type ENUM('call', 'text') NOT NULL DEFAULT 'call',
		duration SMALLINT UNSIGNED
		// other things? );
 * 
 * 
 * @author diego
 *
 */
//FIXME remove this when the class is used
@SuppressWarnings("unused")
public class CallAction {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int CALLACTION_KEY;
	
	//subject_id INT UNSIGNED NOT NULL,
	private int subject_id;
	
	//contact_id INT UNSIGNED NOT NULL,
	private int contact_id;
	
	//created DATETIME NOT NULL,
	private String creationTime;
	
	//TODO: ?
	//act_type ENUM('call', 'text') NOT NULL DEFAULT 'call',
	
	//duration SMALLINT UNSIGNED
	private int duration;
	
}
