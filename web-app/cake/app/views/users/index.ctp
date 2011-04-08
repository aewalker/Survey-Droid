<?php
/*****************************************************************************
 * views/users/index.ctp                                                     *
 *                                                                           *
 * Lists all users                                                           *
 *****************************************************************************/
 
echo $session->flash('auth'); 

	echo $table->startTable('User');
	echo $table->tableBody($users);
	echo $table->endTable(array('Create new user' => array('command' => 'register')));

?>
