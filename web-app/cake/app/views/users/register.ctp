<?php

echo $form->create('User', array('action' => 'register'));

echo $form->input('username');
echo $form->input('passwrd', array('type' => 'password', 'label' => 'Password'));
echo $form->input('password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
echo $form->input('email');
echo $form->end('Submit');

?>