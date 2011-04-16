<?php
/*****************************************************************************
 * models/location.php                                                       *
 *                                                                           *
 * Model for GPS location data.                                              *
 *****************************************************************************/
class Location extends AppModel
{
	//for php4
	var $name = 'Location';
	
	var $belongsTo = 'Subject';
}
?>