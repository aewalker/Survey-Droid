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
	
	var $belongsTo = array('Subject', 'Question');
	var $hasAndBelongsToMany = array('Choice' => array
	(
		'className' => 'Choice'
	));
	
	//TODO probably don't need this; we'll see
	/**
	 * Convinence method to get the type of an answer.
	 * 
	 * Returns the type of the answer with id $id (or the current model id if
	 * $id is not set).  See the constants.php file for detials on what the
	 * return values mean.  Returns false if something didn't work.
	 */
	function getType($id)
	{
		if (empty($id)) //safe because we never use 0 as an id
		{
			$currentID = $this->getID();
			if (empty($currentID)) return false;
			$id = $currentID;
		}
		$result = $this->find('first', array(
			'conditions' => array('id' => $id),
			'fields' => array('ans_type')
		));
		if (empty($result)) return false;
		return $result['Answer']['ans_type'];
	}
}