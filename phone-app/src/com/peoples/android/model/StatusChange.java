/*---------------------------------------------------------------------------*
 * StatusChange.java                                                         *
 *                                                                           *
 * Represents a change in the phone's status such as: being turned off or    *
 * on, or the application being partially or completely disabled.  Currently *
 * unused; left in for future work.                                          *
 *---------------------------------------------------------------------------*/
package com.peoples.android.model;

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
//FIXME remove this when the class is used
@SuppressWarnings("unused")
public class StatusChange {
	
	private static final int GPS = 0;
	private static final int CALL_LOG = 1;
	private static final int TEXT_LOG = 2;
	private static final int WHOLE_APP = 3;
	
	private static final boolean ENABLED = true;
	private static final boolean DISABLED = false;
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int id;
	
	//subject_id INT UNSIGNED NOT NULL,
	private Subject subject;
	
	//created DATETIME NOT NULL,
	private String datetime;
	
	//enabled/disabled
	private boolean status;
	
	// gps, call log, text log, or whole app
	private int feature;
}
