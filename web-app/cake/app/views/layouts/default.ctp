<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN"
	"http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title><?php echo $title_for_layout; ?></title>
	<?php
		echo $html->css('peoples');
		echo $html->meta('peoples.ico', '/img/peoples.ico', array('type' => 'icon'))
	?>
</head>
<body>
<div id="header">
	<h3><a href="/">PEOPLES</a></h3> =>
	<div id="menubar">
		<?php
		if (($user = $session->read('Auth.User')) != NULL)
		{
			echo $html->link('Subjects', array('controller' => 'subjects', 'action' => 'index'));
			echo $html->link('Control Pannel', array('controller' => 'users', 'action' => 'index'));
			//echo $html->link('Data', array('controller' => 'data', 'action' => 'index'));
			//echo $html->link('Surveys', array('controller' => 'surveys', 'action' => 'index'));
		}
		?>
	</div>
</div>
<div id="main">
	<hr />
	<?php echo $content_for_layout; ?>
</div>
<div id="footer">
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
</div>
</body>
</html>