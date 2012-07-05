<?php
/*---------------------------------------------------------------------------*
 * models/extra.php                                                           *
 *                                                                           *
 * Model for texts and phone calls made by/recieved by a subject.            *
 *---------------------------------------------------------------------------*/
/**
 * Model for call logs collected from the phones.
 * 
 * @author Tony Xiao
 */
class Extra extends AppModel
{
	//for php4
	var $name = 'Extra';
	
	//each Extra is "owned" by a subject
	var $belongsTo = 'Subject';
}
?>