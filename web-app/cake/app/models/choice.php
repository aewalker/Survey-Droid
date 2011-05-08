<?php
/*---------------------------------------------------------------------------*
 * models/choice.php                                                         *
 *                                                                           *
 * Model for survey answer choices in multiple choice.                       *
 *---------------------------------------------------------------------------*/
/**
 * Model for a choice that a subject has when answering a survey question.
 * 
 * @author Austin Walker
 */
class Choice extends AppModel
{
	//for php4
	var $name = 'Choice';
	
	var $hasMany = array('Condition', 'Answer');
	var $belongsTo = 'Question';
	
	var $validate = array
	(
		'choice_text' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide choice text'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 255),
				'message' => 'Choice text cannot be longer than 255 characters'
			)
		)
	);
}