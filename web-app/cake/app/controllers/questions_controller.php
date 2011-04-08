<?php 
/*****************************************************************************
 * controllers/questions_controller.php                                      *
 *                                                                           *
 * Controlls all web-end survey functions at the question level.  All        *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class QuestionsController extends AppController
{
	//for php4
	var $name = 'Questions';
	
	var $components = array('Auth');
    var $helpers = array('Table');
    
    //show all questions associated with a particular survey
    function showquestions($surveyid)
    {
    	
    }
    
    //show all the details of a particluar question
    function viewquestion($questionid)
    {
    	
    }
    
    //add a new question to the current survey
    function addquestion($surveyid)
    {
    	
    }
    
    //edit the text of a particular question
    function editquestion($questionid)
    {
    	
    }
    
    //delete a particular question
    function deletequestion($questionid)
    {
    	
    }
}
?>