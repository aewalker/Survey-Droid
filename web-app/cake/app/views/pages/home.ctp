<?php
	if (($user = $session->read('Auth.User')) != NULL)
	{
		?><p>Logged in as <?php
			echo $user['first_name'].' '.$user['last_name'].'. ';
			echo $html->link('Logout', array('controller' => 'users', 'action' => 'logout'));
			?></p>
		<br /><?php
	}
?>
<h1>Welcome to [whatever-we-name-this-thing]</h1>
<?php
	if ($user == NULL)
	{
		?>
		<p>Please log in here:</p>
		<?php echo $html->link('Login', array('controller' => 'users', 'action' => 'login'));
	}
?>
