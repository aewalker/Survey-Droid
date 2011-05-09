<?php
/*---------------------------------------------------------------------------*
 * views/users/register.ctp                                                  *
 *                                                                           *
 * Add a new user.                                                           *
 *---------------------------------------------------------------------------*/
if ($session->read('Auth.User.admin'))
{
	echo $form->create('User', array('action' => 'register'));
	echo $form->input('username');
	echo $form->input('password_copy', array(
		'type' => 'password',
		'label' => 'Password'));
	echo $form->input('password_confirm', array(
		'type' => 'password',
		'label' => 'Confirm the password'));
	echo $form->input('email');
	echo $form->input('first_name');
	echo $form->input('last_name', array('label' => 'Last Name'));
	echo "Make Admin";
	echo $form->checkbox('admin', array('checked' => false));
	echo $form->end('Submit');
}
?>
