package src.com.peoples.model;


/**
 * 
 * CREATE TABLE answers (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	question_id INT UNSIGNED NOT NULL,
	subject_id INT UNSIGNED NOT NULL,
	choice_id INT UNSIGNED,
	ans_text TEXT,
	created DATETIME);
 * 
 * 
 * @author Diego
 *
 */
public class Answer {
	
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int ANSWER_KEY;
	
	//question_id INT UNSIGNED NOT NULL,
	private int question_id;
	
	//subject_id INT UNSIGNED NOT NULL,
	private int subject_int;
	
	//choice_id INT UNSIGNED,
	private int choice_id;
	
	//ans_text TEXT,
	private String text;
	
	//created DATETIME);
	private String datetime;


}
