<?php 
/*****************************************************************************
 * controllers/answers_controller.php                                        *
 *                                                                           *
 * Contains functions that are used by the phone: push and pull to send a    *
 * subjects survey answers and pull new survey data, respectively.           *
 *****************************************************************************/
class AnswersController extends AppController
{
	//for php4
	var $name = 'Answers';
	
	//allow anyone (eg the phones) to use push() and pull()
	var $components = array('Auth' => array
	(
		'authorize' => 'controller',
		'allowedActions' => array('push', 'pull')
	));
	
	function pull()
	{
		
	}
	
	function push()
	{
		
	}
}
?>