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
}
?>