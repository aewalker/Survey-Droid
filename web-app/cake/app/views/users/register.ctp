<?php

echo $form->create('User', array('action' => 'register'));

echo $form->input('username');
echo $form->input('passwrd', array('type' => 'password', 'label' => 'password'));
echo $form->input('confirm_pass', array('type' => 'password', 'label' => 'confirm the password'));
echo $form->input('email');
echo $form->end('Submit');

?>