<?php
/*****************************************************************************
 * models/branch.php                                                         *
 *                                                                           *
 * Model for survey logic branches.                                          *
 *****************************************************************************/
class Branch extends AppModel
{
	//for php4
	var $name = 'Branch';
	
	var $belongsTo = 'Question';
	var $hasMany = 'Condition';
}
?>