<?php 
/*---------------------------------------------------------------------------*
 * controllers/answers_controller.php                                      *
 *                                                                           *
 * Controlls all web-end survey functions at the question level.  All        *
 * functions are ment to be AJAX.                                            *
 *---------------------------------------------------------------------------*/
/**
 * output answers
 * 
 * @author Tony Xiao
 */
App::import('Controller', 'Rest');
class SurveysTakenController extends RestController
{
	//for php4
	var $name = 'SurveysTaken';
	var $components = array('Auth');

    
}

?>