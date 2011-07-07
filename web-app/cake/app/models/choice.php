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
	
	var $hasMany = 'Condition';
	var $belongsTo = 'Question';
	var $hasAndBelongsToMany = array('Answer' => array
	(
		'className' => 'Answer'
	));
	
	var $validate = array
	(
		'choice_text' => array
		(
			'maxLength' => array
			(
				'rule' => array('maxLength', 255),
				'message' => 'Choice text cannot be longer than 255 characters'
			)
		)
	);
	
	//TODO probably don't need this; we'll see
	/**
	 * Convinence method to get the type of a choice.
	 * 
	 * Returns the type of the choice with id $id (or the current model id if
	 * $id is not set).  See the constants.php file for detials on what the
	 * return values mean.  Returns false if something didn't work.
	 */
	function getType($id)
	{
		if (empty($id)) //safe because we never use 0 as an id
		{
			$currentID = $this->getID());
			if (empty($currentID) return false;
			$id = $currentID;
		}
		$result = $this->find('first', array(
			'conditions' => array('id' => $id)),
			'fields' => array('choice_type')
		);
		if (empty($result)) return false;
		return $result['Choice']['choice_type'];
	}
}