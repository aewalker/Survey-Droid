<?php
/*****************************************************************************
 * models/question.php                                                       *
 *                                                                           *
 * Model for survey questions.                                               *
 *****************************************************************************/
class Question extends AppModel
{
	//for php4
	var $name = 'Question';
	
	var $hasMany = array('Choice', 'Branch', 'Condition', 'Answer');
	var $belongsTo = 'Survey';
	
	var $validate = array
	(
		'q_text' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide a question'
			)
		)
	);
}
?>