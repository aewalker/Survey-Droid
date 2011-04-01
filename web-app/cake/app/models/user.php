<?php
/*****************************************************************************
 * models/users.php                                                          *
 *                                                                           *
 * Model for the users table; contains username, password, email, name, etc. *
 *****************************************************************************/
class User extends AppModel
{
	/*$_schema = array
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
		)
		'last_name' => array
		(
			'type' => 'string'
		),
		'admin' => array
		(
			'type' => 'boolean'
		)
	);*/
	
	var $validate = array
	(
		'username' => array
		( //Usernames must be alpha-numeric and between 5 and 20 characters
			'content' => array
			(
				'rule' => 'alphaNumeric',
				'required' => true,
				'message' => 'Usernames must be contain only letters and numbers'
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
		'passwrd' => array
		( //Passwords must be between 8 and 20 characters and must match confirm_pass at registration
			'matchesConfirmPassword' => array
			(
				'rule' => array('identicalFieldValues', 'password_confirm'),
				'message' => 'Passwords must match'
			),
			'minLength' => array
			(
				'rule' => array('minLength', 8),
				'message' => 'Passwords must be at least 8 characters long'
			)/*,
			'maxLength' => array
			(
				'rule' => array('maxLength', 20),
				'message' => 'Passwords cannot be longer than 20 characters'
			)*/
			
		)/*,
		'password_confirm' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 8),
				'message' => 'Passwords must be at least 8 characters long'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 20),
				'message' => 'Passwords cannot be longer than 20 characters'
			)
		)*/
	);
	
	//checks that two fields are equal
	//code by aranworld: http://bakery.cakephp.org/articles/aranworld/2008/01/14/using-equalto-validation-to-compare-two-form-fields
	function identicalFieldValues( $field=array(), $compare_field=null ) 
    {
        foreach( $field as $key => $value ){
            $v1 = $value;
            $v2 = $this->data[$this->name][ $compare_field ];                 
            if($v1 !== $v2) {
                return FALSE;
            } else {
                continue;
            }
        }
        return TRUE;
    } 
    
	function validateConfirmPassword($data) 
	{
		if ($this->data['User']['passwrd'] == AuthComponent::password($this->data['User']['password_confirm'])) 
			return true;
		else
			return false;
	}
}
?> 
