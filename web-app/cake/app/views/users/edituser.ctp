<?php

echo $form->create('User', array('action' => 'edituser'));


echo $form->input('username', array('default' => $this->data['User']['username']) );
echo $form->input('password_copy', array('type' => 'password', 'label' => 'Password'));
echo $form->input('password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
echo $form->input('email');
echo $form->input('first_name');
echo $form->input('last_name', array('label' => 'Last Name'));
echo $form->checkbox('admin', array('hiddenField' => false, 'label' => 'Make admin')); 
echo $form->end('Submit');

?>