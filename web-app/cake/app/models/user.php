<?php
/*---------------------------------------------------------------------------*
 * models/users.php                                                          *
 *                                                                           *
 * Model for the users table; contains username, password, email, name, etc. *
 *---------------------------------------------------------------------------*/
/**
 * Model for web users.
 * 
 * @author Austin Walker
 */
class User extends AppModel
{
	//TODO I still can't figure out if this is needed...
	public $_schema = array
	(
		'id' => array
		(
			'type' => 'primary_key'
		),
		'username' => array
		(
			'type' => 'string',
			'length' => 20
		),
		'email' => array
		(
			'type' => 'string',
			'length' => 320
		),
		'password' => array
		(
			'type' => 'string',
			'length' => 41
		),
		'first_name' => array
		(
			'type' => 'string'
		),
		'last_name' => array
		(
			'type' => 'string'
		),
		'admin' => array
		(
			'type' => 'boolean'
		)
	);
	
	var $validate = array
	(
		'username' => array
		( //Usernames must be alpha-numeric and between 5 and 20 characters
			'content' => array
			(
				'rule' => 'alphaNumeric',
				'required' => true,
				'message' => 'Username must be contain only alphanumerics'
			),
			'minLength' => array
			(
				'rule' => array('minLength', 5),
				'message' => 'Usernames must be at least 5 characters long'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 20),
				'message' => 'Usernames cannot be longer than 20 characters'
			)
		),
		'email' => array
		(
			'email' => array
			(
				'rule' => 'email',
				'message' => 'Email must be a valid email address'
			)
		),
		'password_copy' => array
		( //Passwords must be between 8 and 20 characters and must match
		  //confirm_pass at registration
			'minLength' => array
			(
				'rule' => array('minLength', 8),
				'message' => 'Passwords must be at least 8 characters long'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 20),
				'message' => 'Passwords cannot be longer than 20 characters'
			),
			'matchesConfirmPassword' => array
			(
				'rule' => array('identicalFieldValues', 'password_confirm'),
				'message' => 'Passwords must match'
			)
			
		)
	);
}
?>