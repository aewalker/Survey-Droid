<?php
	echo $session->flash('auth'); 
	echo $form->create('User', array('action' => 'login'));
	echo $form->input('username');
	echo $form->input('password', array('type'=>'password'));
	echo $form->end('Login');
?>
