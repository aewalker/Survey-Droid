<?php
/*****************************************************************************
 * models/call.php                                                           *
 *                                                                           *
 * Model for texts and phone calls made by/recieved by a subject.            *
 *****************************************************************************/
class Call extends AppModel
{
	//for php4
	var $name = 'Call';
	
	var $belongsTo = 'Subject';
}
?>