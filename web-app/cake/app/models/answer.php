<?php
/*---------------------------------------------------------------------------*
 * models/answer.php                                                         *
 *                                                                           *
 * Model for survey responses.                                               *
 *---------------------------------------------------------------------------*/
/**
 * Model for the answer given by a phone user to a survey question.
 * 
 * @author Austin Walker
 */
class Answer extends AppModel
{
	//for php4
	var $name = 'Answer';
	
	var $belongsTo = array('Subject', 'Question', 'Choice' => array
	(
		'className' => 'Choice',
		//only associate a choice with an answer if Answer.text is NULL because
		//either a question is multiple choice (so an answer will have a
		//choice), or it is freeresponse, meaning that it's ans_text field will
		//contain content.
		'conditions' => array('Choice.choice_text' => NULL)
	));
}