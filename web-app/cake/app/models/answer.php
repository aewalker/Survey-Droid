<?php
/*****************************************************************************
 * models/answer.php                                                         *
 *                                                                           *
 * Model for survey responses.                                               *
 *****************************************************************************/
class Answer extends AppModel
{
	//for php4
	var $name = 'Answer';
	
	var $belongsTo = array('Subject', 'Question', 'Choice' => array
	(
		'className' => 'Choice',
		//only associate a choice with an answer if Answer.text is NULL because either a
		//question is multiple choice (so an answer will have a choice), or it is free
		//response, meaning that it's ans_text field will contain content.
		'conditions' => array('Choice.choice_text' => NULL)
	));
}