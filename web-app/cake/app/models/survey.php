<?php
/*---------------------------------------------------------------------------*
 * models/survey.php                                                         *
 *                                                                           *
 * Model for surveys.                                                        *
 *---------------------------------------------------------------------------*/
/**
 * Model for the surveys presented to phone users.
 * 
 * @author Austin Walker
 */
class Survey extends AppModel
{
	//for php4
	var $name = 'Survey';
	
	//the survey has a first question; to Cake, this means the survey "belongs
	//to" that question
	var $belongsTo = 'Question';
	
	var $hasMany = 'Question';
	
	var $validate = array
	(
		'name' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide a survey name'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 255),
				'message' => 'Survey name cannot be longer than 255 characters'
			)
		)
		//TODO Probably should add validation for days of week...
	);
}
?>