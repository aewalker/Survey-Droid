<?php
/*****************************************************************************
 * models/subject.php                                                        *
 *                                                                           *
 * Model for the subjects table; contains name, phone number, etc.           *
 *****************************************************************************/
class Subject extends AppModel
{
	//for php4
	var $name = 'Subject';
	
	var $validate = array
	(
		'phone_num' => array
		(
			//change from 'us' if you want to use international phone numbers
			'rule' => array('phone', null, 'us')
		)
	);
}
?>