<?php
/*****************************************************************************
 * models/status_change.php                                                  *
 *                                                                           *
 * Model for data about a subjects enabling/disabling of the application.    *
 *****************************************************************************/
class StatusChange extends AppModel
{
	//for php4
	var $name = 'StatusChange';
	
	var $belongsTo = 'Subject';
}
?>