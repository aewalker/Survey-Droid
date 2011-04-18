package com.peoples.android.model;


/**
 * 
 * 
 * CREATE TABLE questions (
	id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	text TEXT);
 * 
 * 
 * @author Diego
 *
 */
public class Question {
	
	//id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,
	private int QUESTION_KEY;
	
	//text TEXT
	
	//The adapter we are using to display choices in the survey GUI
	//requires a String array for the choices. Database ppl: is it 
	//possible to have such an array passed as part of the question
	//data type? it would make my life easier :) <3 panda
	private String[] CHOICES;
	private int numberOfChoices;
	private int nextQuestionID;
	
	//This is a completely bogus constructor make entirely for
	//testing purposes.
	private String questionText;
	public Question(String q, String a1, String a2, String a3)
	{
		CHOICES = new String[3];
		CHOICES[0] = a1;
		CHOICES[1] = a2;
		CHOICES[2] = a3;
		questionText = q;
	}
	
	public String[] getChoices() {
		return CHOICES;
	}
	
	public String getQuestionText() {
		return questionText;
	}
	public void setNextQuestionID(int id)
	{
		nextQuestionID = id;
	}
	
	public int getNextQuestionID()
	{
		return nextQuestionID;
	}
	private String text;

}
	

