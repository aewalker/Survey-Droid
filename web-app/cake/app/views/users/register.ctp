<?php
create(‘User’, array(‘action’ => ‘register’));

echo $form->input(‘username’, array(‘between’ => ‘Pick a username’));

echo $form->input(‘passwrd’, array(‘type’ => ‘password’, ‘label’ => ‘Password’));
echo $form->input(‘email’, array(‘between’ => ‘We need to send you a confirmation email to check you are human’));
echo $form->submit(‘Submit’);
echo $form->end();

?>