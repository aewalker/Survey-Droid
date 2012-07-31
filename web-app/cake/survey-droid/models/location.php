<?php
/*---------------------------------------------------------------------------*
 * models/location.php                                                       *
 *                                                                           *
 * Model for GPS location data.                                              *
 *---------------------------------------------------------------------------*/
/**
 * Location model for GPS data from phones.
 * 
 * @author Austin Walker
 */
class Location extends AppModel
{
	//for php4
	var $name = 'Location';
	
	//each location data point belogs to a subject
	var $belongsTo = 'Subject';
}
?>