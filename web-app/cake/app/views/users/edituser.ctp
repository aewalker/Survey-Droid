<?php

echo $form->create('User', array('action' => 'edituser'));

if (($user = $session->read('Auth.User')) != NULL)
{
	echo $form->input('username', array('default' => $user['username']) );
	echo $form->input('password_copy', array('type' => 'password', 'label' => 'Password'));
	echo $form->input('password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
	echo $form->input('email', array('default' => $user['email']));
	echo $form->input('first_name', array('label' => 'First Name', 'default' => $user['first_name']));
	echo $form->input('last_name', array('label' => 'Last Name', 'default' => $user['last_name']));
	echo "Make Admin";
	if($user['admin']==1)
		echo $form->checkbox('admin', array('value' => yes));
	else
		echo $form->checkbox('admin', array('value' => yes));
	 
	echo $form->end('Submit');
}


?>