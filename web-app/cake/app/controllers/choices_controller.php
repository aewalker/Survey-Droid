<?php 
/*****************************************************************************
 * controllers/choices_controller.php                                        *
 *                                                                           *
 * Controlls all web-end survey functions at the choice level.  All          *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class ChoicesController extends AppController
{
	//for php4
	var $name = 'Choices';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    //show all choices related to a particular question
    function showchoices($questionid)
    {
    	
    }
    
    //add a new choice to the current question
    function addchoice($questionid)
    {
    	
    }
    
    //edit a particular choice
    function editchoice($choiceid)
    {
    	
    }
    
	//delete a particular choice
    function deletechoice($choiceid)
    {
    	
    }
}
?>