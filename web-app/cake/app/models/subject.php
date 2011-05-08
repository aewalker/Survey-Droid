<?php
/*---------------------------------------------------------------------------*
 * models/subject.php                                                        *
 *                                                                           *
 * Model for the subjects table; contains name, phone number, etc.           *
 *---------------------------------------------------------------------------*/
/**
 * Model for a phone user, aka. a subject.
 * 
 * @author Austin Walker
 */
class Subject extends AppModel
{
	//for php4
	var $name = 'Subject';
	
	//enables look up all the answers a subject has given from within the
	//subjects controller
	var $hasMany = 'Answer';
	
	var $validate = array
	(
//		'phone_num' => array
//		(
//			//change from 'us' if you want to use international phone numbers
//			'rule' => array('phone', null, 'us'),
//			'message' => 'Please provide a valid US phone number'
//		),
		'first_name' => array
		(
			'maxLength' => array
			(
				'rule' => array('maxLength', 320),
				'message' => 'First name cannot be longer than 320 characters'
			)
		),
		'last_name' => array
		(
			'maxLength' => array
			(
				'rule' => array('maxLength', 320),
				'message' => 'Last name cannot be longer than 320 characters'
			)
		)
	);
}
?>