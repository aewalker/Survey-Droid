<?php
/*---------------------------------------------------------------------------*
 * models/status_change.php                                                  *
 *                                                                           *
 * Model for data about a subjects enabling/disabling of the application.    *
 *---------------------------------------------------------------------------*
 * Note: this hasn't been implemented on the phone side yet.                 *
 *---------------------------------------------------------------------------*/
/**
 * Model for a change in a phone status (eg the phone turning off or on).
 * 
 * @author Austin Walker
 */
class StatusChange extends AppModel
{
	//for php4
	var $name = 'StatusChange';
	
	var $belongsTo = 'Subject';
}
?>