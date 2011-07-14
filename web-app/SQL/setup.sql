/* SQL to set up or upgrade the Databse */

DROP TABLE IF EXISTS users;
CREATE TABLE users (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(20) NOT NULL UNIQUE,
	email VARCHAR(320) NOT NULL UNIQUE,
	password CHAR(41) NOT NULL,
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	admin TINYINT(1) DEFAULT 0);

DROP TABLE IF EXISTS subjects;	
CREATE TABLE subjects (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	phone_num VARCHAR(13),
	first_name VARCHAR(255),
	last_name VARCHAR(255),
	device_id VARCHAR(255) /* serial number of the phone */);

DROP TABLE IF EXISTS surveys;
CREATE TABLE surveys (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255),
	created DATETIME,
	question_id INT UNSIGNED NOT NULL, /* the first question in the survey */
	subject_init TINYINT DEFAULT 0, /* can subjects initiate this survey? (1 for yes, 0 for no) */ 
	/* field for each day; holds times in 24 hour format separtated by commas */
	mo VARCHAR(255),
	tu VARCHAR(255),
	we VARCHAR(255),
	th VARCHAR(255),
	fr VARCHAR(255),
	sa VARCHAR(255),
	su VARCHAR(255));

DROP TABLE IF EXISTS questions;
CREATE TABLE questions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	survey_id INT UNSIGNED NOT NULL,
	/* SINGLE_CHOICE = 0, MULTI_CHOICE = 1, SCALE_TEXT = 2, SCALE_IMG = 3, FREE_RESPONSE = 4 */
	q_type TINYINT NOT NULL,
	q_text TEXT NOT NULL,
	q_img_low TEXT,
	q_img_high TEXT,
	q_text_low TEXT,
	q_text_high TEXT);

DROP TABLE IF EXISTS branches;
CREATE TABLE branches (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	/* parent question */
	question_id INT UNSIGNED NOT NULL, /*foreign keys*/
	/* child question */
	next_q INT UNSIGNED NOT NULL);

DROP TABLE IF EXISTS conditions;
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

DROP TABLE IF EXISTS choices;
CREATE TABLE choices (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	choice_type TINYINT NOT NULL, /* text = 0, image = 1 */
	choice_text VARCHAR(255),
	choice_img TEXT,
	question_id INT UNSIGNED);

DROP TABLE IF EXISTS answers;
CREATE TABLE answers (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	question_id INT UNSIGNED NOT NULL,
	subject_id INT UNSIGNED NOT NULL,
	ans_type TINYINT NOT NULL, /* CHOICE = 0, VALUE = 1, TEXT = 2 */
	ans_text TEXT,
	and_value INT,
	created DATETIME);

/* join table between asnwers and choices: the two have a HABTM relationship */
DROP TABLE IF EXISTS answers_choices;
CREATE TABLE answers_choices (
	answer_id INT UNSIGNED NOT NULL,
	choice_id INT UNSIGNED NOT NULL);
	
	
DROP TABLE IF EXISTS locations;
CREATE TABLE locations (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	longitude DOUBLE NOT NULL,
	latitude DOUBLE NOT NULL);

DROP TABLE IF EXISTS calls;
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

DROP TABLE IF EXISTS status_changes;
CREATE TABLE status_changes (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	/* 1 for enabled, 0 for disabled */
	status TINYINT NOT NULL,
	/* gps 0, call log, 1, text log 2, surveys 3 */
	feature TINYINT NOT NULL);

/* This is actually a pretty common way to set up config (used by, eg., freeradius). */
DROP TABLE IF EXISTS configurations;
CREATE TABLE configurations (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	opt TINYTEXT NOT NULL, /* We might need this later. */
	c_key TEXT NOT NULL, /* because key and value are reserved... */
	c_value TEXT NOT NULL);
