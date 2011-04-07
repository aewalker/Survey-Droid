<?php
/*****************************************************************************
 * models/survey.php                                                         *
 *                                                                           *
 * Model for surveys.                                                        *
 *****************************************************************************/
class Survey extends AppModel
{
	//for php4
	var $name = 'Survey';
	
	var $belongsTo = 'Question';
	
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
				'message' => 'Survey names cannot be longer than 255 characters'
			)
		)
		//Probably should add validation for days of week...
	);
}
?>