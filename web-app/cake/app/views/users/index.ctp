<?php
/*---------------------------------------------------------------------------*
 * views/users/index.ctp                                                     *
 *                                                                           *
 * Lists all users for admins.                                               *
 *---------------------------------------------------------------------------*/
if($session->read('Auth.User.admin'))
{
	echo $this->Session->flash();
	
	echo $table->startTable('User');
	echo $table->tableBody($users);
	echo $table->endTable(array('Create new user' => array(
		'command' => 'register', 'arg' => NULL, 'type' => 'link')));
}
else
{
	echo "You don't have permission to see this page!";
}
?>
