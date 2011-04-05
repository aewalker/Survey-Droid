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
			'rule' => array('phone', null, 'us'),
			'message' => 'Please provide a phone number'
		),
		'first_name' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide a first name'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 320),
				'message' => 'First name cannot be longer than 320 characters'
			)
		),
		'last_name' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide a last name'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 320),
				'message' => 'Last name cannot be longer than 320 characters'
			)
		)
	);
}
?>