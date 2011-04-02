<?php
/*
echo $form->create('User', array('action' => 'edituser'));

if (($user = $session->read('Auth.User')) != NULL)
{
	echo $form->input('username', array('default' => $user['username']) );
	echo $form->input('password_copy', array('type' => 'password', 'label' => 'Password'));
	echo $form->input('password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
	echo $form->input('email', array('default' => $user['email']));
	echo $form->input('first_name', array('label' => 'First Name', 'default' => $user['first_name']));
	echo $form->input('last_name', array('label' => 'Last Name', 'default' => $user['last_name']));
	echo "Make Admin";
	if($user['admin']==1)
		echo $form->checkbox('admin', array('checked' => true));
	else
		echo $form->checkbox('admin', array('checked' => false));
		
	echo $form->input('id', array('hidden' => $user['id']));
	//echo $form->input('password', array('hidden' => $user['password']));
	 
	echo $form->end('Submit');
}

*/
?>
<?php

$this->set("title_for_layout", array('title' => 'Edit User'));

?><div id="content">
	<?php 
	if (!isset($result))
	{ 
		$user = $user['User'];
		echo $form->create('User', array('url' => '/users/edit/'.$user['id']));
		echo $form->input('User.username', array('default' => $user['username']) );
		echo $form->input('User.password_copy', array('type' => 'password', 'label' => 'Password'));
		echo $form->input('User.password_confirm', array('type' => 'password', 'label' => 'Confirm the password'));
		echo $form->input('User.email', array('default' => $user['email']));
		echo $form->input('User.first_name', array('label' => 'First Name', 'default' => $user['first_name']));
		echo $form->input('User.last_name', array('label' => 'Last Name', 'default' => $user['last_name']));
		echo "Make Admin";
		if($user['admin']==1)
			echo $form->checkbox('User.admin', array('checked' => true));
		else
			echo $form->checkbox('User.admin', array('checked' => false));
			
		echo $form->hidden('User.id', array('value' => $user['id']));
		
		echo $form->end('Submit');
			
	}
	else
	{ //Form was submited
		if ($result === true)
		{ //Everything worked
			echo '<h3>Success!  Story changed.</h3>';
			echo '<h3>'.$html->link('Back', '/users/').'</h3>';
		}
	}
	 ?>
</div>
