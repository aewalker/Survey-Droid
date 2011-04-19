package com.peoples.android.model;

import android.util.Log;


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
	private int id;
	
	//text TEXT
	
	//The adapter we are using to display choices in the survey GUI
	//requires a String array for the choices. Database ppl: is it 
	//possible to have such an array passed as part of the question
	//data type? it would make my life easier :) <3 panda
	private String[] CHOICES;
	private int numberOfChoices;
	private int nextQuestionID;
	private Branch[] BRANCHES;
	
	public Answer answer;
	private int type; // 0 if multiple choice, 1 if free response
	
	//This is a completely bogus constructor make entirely for
	//testing purposes.
	private String questionText;
	public Question(int key, String q, String[] choices, Branch[] branches)
	{
		id = key;
		if (branches != null)
		{
			BRANCHES = new Branch[branches.length];
			for (int i = 0; i < branches.length; i++)
			{
				BRANCHES[i] = branches[i];
			}
		}
		
		questionText = q;
		if (choices != null)
		{
			numberOfChoices = choices.length;
			CHOICES = new String[numberOfChoices];
			for (int i = 0; i < numberOfChoices; i++)
			{
				CHOICES[i] = choices[i];
			}
			type = 0;
		}
		else
		{
			/*CHOICES = new String[1];
			CHOICES[0] = "Enter your response here";*/
			type = 1;
		}
	}
	
	public int getId() {
	    Log.d("Question", Integer.toString(id));
	    return id;
	}
	
	public String getAnswer() {
		if (answer == null)
			return "No answer found for question";
		else return answer.getText();
	}
	
	public void setAnswer(int c) {
		answer = new Answer(c);
	}
	
	public void setAnswer(String r) {
		answer = new Answer(r);
	}
	
	
	public int getQuestionKey() {
		return id;
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
	
	public int getType() {
		return type;
	}
	
}
	

