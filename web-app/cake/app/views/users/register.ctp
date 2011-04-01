<?php
echo $session->flash('auth'); 
echo $form->create(‘User’, array(‘action’ => ‘register’));

echo $form->input(‘username’);
echo $form->input(‘passwrd’);
echo $form->input(‘email’);
echo $form->end(‘Submit’);

?>