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
	
	//this controller is associated with all the models that the phones use
	var $uses = array('Survey', 'Answer', 'Location', 'StatusChange', 'Call');
	
	var $layout = 'json';
	
	//allow anyone (eg the phones) to use push() and pull()
	var $components = array('Auth' => array
	(
		'authorize' => 'controller',
		'allowedActions' => array('push', 'pull')
	));
	
	//pull survey data (and descendants) from the database
	function pull()
	{
		
	}
	
	//push answers, locations, statuschanges, and calls to the database
	function push()
	{
		
	}
	
	/* some notes:
	 * 
	 * convert an array to json => use js helper: $Js->value($array);
	 * 
	 * DATETIME <=> Unix timestamp => see http://snippets.dzone.com/posts/show/1455
	 */
}
?>