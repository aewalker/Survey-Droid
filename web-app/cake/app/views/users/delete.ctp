<?php

?><div id="content">
	<?php //if ($this->Session->check('User.isAdmin'))
	//{
		if (!isset($result))
		{
			echo '<p><strong>Are you sure you want to delete this user?</strong></p>';
			echo $form->create('User', array('url' => '/users/delete/'.$id));
			echo $form->hidden('confirm', array('value' => 'true'));
			echo $form->submit('Yes');
			echo $form->end();
			echo $form->create('User', array('url' => '/users/'));
			echo $form->submit('No');
			echo $form->end();
		}
		else
		{
			if ($result == true)
			{
				echo '<h3>User is deleted.</h3>';
			}
			else
			{
				echo '<h3>Failure!  Could not delete the user.</h3>';
			}
			echo '<h3>'.$html->link('Back', '/users/').'<h3>';
		}
	//}
	 ?>
</div>