/* SQL to set up the Databse */

CREATE TABLE users (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(20) NOT NULL UNIQUE,
	email VARCHAR(320) NOT NULL UNIQUE,
	password CHAR(41) NOT NULL,
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	admin TINYINT(1) DEFAULT 0);
	
CREATE TABLE subjects (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	phone_num VARCHAR(13),
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	device_id VARCHAR(255) /* serial number of the phone */);
	
CREATE TABLE surveys (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	created DATETIME,
	question_id INT UNSIGNED NOT NULL, /* the first question in the survey */
	/* field for each day; holds times in 24 hour format separtated by commas */
	mo VARCHAR(255),
	tu VARCHAR(255),
	we VARCHAR(255),
	th VARCHAR(255),
	fr VARCHAR(255),
	sa VARCHAR(255),
	su VARCHAR(255));
	
CREATE TABLE questions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	survey_id INT UNSIGNED NOT NULL,
	q_type TINYINT NOT NULL,
	q_text TEXT NOT NULL,
	q_img_low TEXT,
	q_img_high TEXT,
	q_text_low TEXT,
	q_text_high TEXT);
	
CREATE TABLE branches (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	/* parent question */
	question_id INT UNSIGNED NOT NULL, /*foreign keys*/
	/* child question */
	next_q INT UNSIGNED NOT NULL);
	
CREATE TABLE conditions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	/* branch this condition belongs to */
	branch_id INT UNSIGNED NOT NULL,
	/* question this condition references */
	question_id INT UNSIGNED NOT NULL,
	/* choice that is required as the answer for the above question for this condition to be true */
	choice_id INT UNSIGNED NOT NULL,
	/* type of condition: 0 for answer given in current survey, 1 for answer given at some time in a previous
	   survey, and 2 for an answer never given in a previous survey. */
	type TINYINT UNSIGNED NOT NULL);
	
CREATE TABLE choices (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	choice_type TINYINT NOT NULL,
	choice_text VARCHAR(255),
	choice_img TEXT,
	question_id INT UNSIGNED);
	
CREATE TABLE answers (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	question_id INT UNSIGNED NOT NULL,
	subject_id INT UNSIGNED NOT NULL,
	choice_ids VARCHAR(255),
	ans_text TEXT,
	and_value INT,
	created DATETIME);
	
	
CREATE TABLE locations (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	longitude DOUBLE NOT NULL,
	latitude DOUBLE NOT NULL);
	
CREATE TABLE calls (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	/* phone serial number plus the contact number in that phone */
	contact_id VARCHAR(255) NOT NULL,
	created DATETIME NOT NULL,
	/* outgoing call 0, incoming call 1, outgoing text 2, incoming text 3, missed call 4 */
	type TINYINT NOT NULL,
	/* call duration in seconds, NULL for texts, missed calls */
	duration SMALLINT UNSIGNED);
	
CREATE TABLE status_changes (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	/* 1 for enabled, 0 for disabled */
	status TINYINT NOT NULL,
	/* gps 0, call log, 1, text log 2, whole app 3 */
	feature TINYINT NOT NULL);

/* This is actually a pretty common way to set up config (used by, eg., freeradius). */
CREATE TABLE configurations (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	opt TINYTEXT NOT NULL DEFAULT "==", /* We might need this later. */
	key TEXT NOT NULL,
	value TEXT NOT NULL);