/* SQL to set up the Databse */

USE socioapp;

CREATE TABLE researchers (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	email VARCHAR(320) NOT NULL UNIQUE,
	pass CHAR(41) NOT NULL,
	first_name VARCHAR(255),
	last_name VARCHAR(255)
	/* other things? */);
	
CREATE TABLE subjects (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	phone_num VARCHAR(13) NOT NULL,
	first_name VARCHAR(255) NOT NULL,
	last_name VARCHAR(255) NOT NULL
	/* other things? */);
	
CREATE TABLE contacts (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL);
	
CREATE TABLE surveys (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	name VARCHAR(255));
	
CREATE TABLE questions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	survey_id INT UNSIGNED NOT NULL,
	ques_type ENUM('choice', 'free_resp', 'auto'/* insert other question types here */) NOT NULL,
	text TINYTEXT,
	question_id INT UNSIGNED);
	
CREATE TABLE options (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	text VARCHAR(255),
	question_id INT UNSIGNED NOT NULL,
	next_ques INT UNSIGNED);
	
CREATE TABLE location_actions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	longitude DOUBLE NOT NULL,
	lattitude DOUBLE NOT NULL);
	
CREATE TABLE call_actions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	contact_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	act_type ENUM('call', 'text') NOT NULL DEFAULT 'call',
	duration SMALLINT UNSIGNED
	/* other things? */);
	
CREATE TABLE status_changes (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	subject_id INT UNSIGNED NOT NULL,
	created DATETIME NOT NULL,
	status ENUM('enabled', 'disbled') NOT NULL,
	feature ENUM('gps', 'call log', 'text log', 'app') NOT NULL);
