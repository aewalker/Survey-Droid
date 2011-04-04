<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title><?php echo $title_for_layout; ?></title>
</head>
<body>
<h3>PEOPLES</h3>
<hr />
<?php echo $content_for_layout; ?>
<hr />
<?php
	if (($user = $session->read('Auth.User')) != NULL)
	{
		echo '<p>Logged in as ';
		echo $user['first_name'].' '.$user['last_name'].'. ';
		if($user['admin']==1)
			echo ' (Administrator)';
		echo $html->link('Logout', array('controller' => 'users', 'action' => 'logout'));
		echo '</p>';
	}
	else
	{
		echo '<p>Please log in here: ';
		echo $html->link('Login', array('controller' => 'users', 'action' => 'login'));
		echo '</p>';
	}
?>
<hr />
</body>
</html>