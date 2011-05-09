<?php
/*---------------------------------------------------------------------------*
 * models/condition.php                                                      *
 *                                                                           *
 * Model for survey branch conditions.                                       *
 *---------------------------------------------------------------------------*/
/**
 * Model for the conditions in a survey branch.
 * 
 * @author Austin Walker
 */
class Condition extends AppModel
{
	//for php4
	var $name = 'Condition';
	
	//each condition belongs to a branch and points to a question and a choice
	var $belongsTo = array('Branch', 'Question', 'Choice');
	
	//note that the type field is either
	//0 for 'just was',
	//1 for 'ever was', or
	//2 for 'never has been'
}
?>