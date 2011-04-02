<?php

$this->set("title_for_layout", array('title' => 'Users'));
?><div id="content">
	<h1>All Users</h1>
	<?php
	foreach ($users as $user)
	{
		echo '<p>'.$html->link($user['username'], '/users/view/'.$user['id']).' <strong>';
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
			echo '<h3>'.$html->link('Create New User', '/users/register/');
		//} 
		?>
</div>