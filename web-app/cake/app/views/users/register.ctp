<?php

echo $form->create('User', array('action' => 'register'));

echo $form->input('username');
echo $form->input('password_copy', array('type' => 'password', 'label' => 'Password'));
echo $form->input('password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
echo $form->input('email');
echo $form->input('first_name', array('label' => 'First Name'));
echo $form->input('last_name', array('label' => 'Last Name'));
echo $form->input('admin', array('multiple' => 'checkbox', 'label' => 'Make admin'));
echo $form->end('Submit');

?>
