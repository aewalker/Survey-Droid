package com.peoples.model;

public class Survey {
	
	
	/**id INT UNSIGNED NOT NULL AUTO_INCREMENT PRIMARY KEY,**/
	private int SURVEY_KEY;
	
	/**name VARCHAR(255)),**/
	private String name;
	
	/**created DATETIME,**/
	private String DATETIME;
	
	/**first_q INT UNSIGNED NOT NULL**/
	private Question first_q;
	
	/**field for each day; holds times in 24 hour format separtated by commas
	mo VARCHAR(255),
	tu VARCHAR(255),
	we VARCHAR(255),
	th VARCHAR(255),
	fr VARCHAR(255),
	sa VARCHAR(255),
	su VARCHAR(255));**/
	private String mo, tu, we, th, fr, sa, su;


}
