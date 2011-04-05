<?php
/*****************************************************************************
 * views/users/index.ctp                                                     *
 *                                                                           *
 * Lists all users                                                           *
 *****************************************************************************/
echo $this->Session->flash();

echo $table->startTable('All Users');
echo $table->tableBody($users);
echo $table->endTable(array('Create new user' => array('command' => 'register')));
?>


/!--<div id="content">
	<h3>All Users</h3>
	<?php
	foreach ($users as $user)
	{
		echo '<p>'.$user['username'].'&nbsp;';
		//if ($this->Session->check('User.isAdmin'))
		//{
			echo ' '.$html->link('Edit', '/users/edituser/'.$user['id']).' '.
			$html->link('Delete', '/users/deleteuser/'.$user['id']);
		//}
		echo '</strong></p>';
	} ?>
</div>
<div id="sidebar">
	<?php //if ($this->Session->check('User.isAdmin'))
		//{
			echo '<h4>'.$html->link('Create New User', '/users/register/').'</h4>';
		//} 
		?>
</div>--/