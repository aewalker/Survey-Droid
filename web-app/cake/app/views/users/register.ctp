<?php
create(‘User’, array(‘action’ => ‘register’));

echo $form->input(‘username’, array(‘between’ => ‘Username’) );

echo $form->input(‘passwrd’, array(‘type’ => ‘password’, ‘label’ => ‘Password’));
echo $form->input(‘email’, array(‘between’ => ‘Email address’));
echo $form->submit(‘Submit’);
echo $form->end();

?>