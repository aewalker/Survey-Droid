<?php
/*---------------------------------------------------------------------------*
 * models/call.php                                                           *
 *                                                                           *
 * Model for texts and phone calls made by/recieved by a subject.            *
 *---------------------------------------------------------------------------*/
/**
 * Model for call logs collected from the phones.
 * 
 * @author Austin Walker
 */
class Call extends AppModel
{
	//for php4
	var $name = 'Call';
	
	//each call is "owned" by a subject
	var $belongsTo = 'Subject';
}
?>