<?php

echo $form->create(‘User’, array(‘action’ => ‘register’));

echo $form->input(‘username’);
echo $form->input(‘passwrd’, array(‘type’ => ‘password’, ‘label’ => ‘Password’));
echo $form->input(‘email’, array(‘between’ => ‘Email address’));
echo $form->end(‘Submit’);

?>