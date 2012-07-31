<?php
/*---------------------------------------------------------------------------*
 * models/branch.php                                                         *
 *                                                                           *
 * Model for survey logic branches.                                          *
 *---------------------------------------------------------------------------*/
/**
 * Model for branches in a survey.
 * 
 * @author Austin Walker
 */
class Branch extends AppModel
{
	//for php4
	var $name = 'Branch';
	
	//each branch is "owned" by a question
	var $belongsTo = 'Question';
	
	//each branch can have multiple conditions
	var $hasMany = 'Condition';
}
?>