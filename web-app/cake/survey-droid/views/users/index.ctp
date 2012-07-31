<?php
/*---------------------------------------------------------------------------*
 * views/users/index.ctp                                                     *
 *                                                                           *
 * Lists all users for admins.                                               *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

echo $table->startTable('User');
echo $table->tableBody($users);
echo $table->endTable(array('Create new user' => array(
	'command' => 'register', 'arg' => NULL, 'type' => 'link')));
?>
