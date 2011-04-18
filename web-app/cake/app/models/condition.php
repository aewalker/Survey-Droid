<?php
/*****************************************************************************
 * models/condition.php                                                      *
 *                                                                           *
 * Model for survey branch conditions.                                       *
 *****************************************************************************/
class Condition extends AppModel
{
	//for php4
	var $name = 'Condition';
	
	var $belongsTo = array('Branch', 'Question', 'Choice');
	
	//note that the type field is either
	//0 for 'just was',
	//1 for 'ever was', or
	//2 for 'never has been'
}
?>