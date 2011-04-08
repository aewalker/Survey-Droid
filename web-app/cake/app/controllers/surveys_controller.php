<?php 
/*****************************************************************************
 * controllers/surveys_controller.php                                        *
 *                                                                           *
 * Controlls all web-end survey functions include functions for all of the   *
 * components of a survey (questions, choices, branches, conditions).        *
 *****************************************************************************/
class SurveysController extends AppController
{
	//for php4
	var $name = 'Surveys';
	
	var $components = array('Auth');
    var $helpers = array('Table');
    
    /*------------------------------*/
    /*--- Survey level functions ---*/
    /*------------------------------*/
    
    //show all surveys in a table
    function index()
    {
    	
    }
    
    //show the details of a particular survey
    function viewsurvey($surveyid)
    {
    	
    }
    
    //add a new survey
    function addsurvey()
    {
    	
    }
    
    //edit the name of a particular survey
    //all functions below this point (except deletesurvey()) will be AJAX-based
    //with this function generating the page that they are on.
    function editsurvey($surveyid)
    {
    	
    }
    
    //delete a particular survey and it's associated data
    function deletesurvey($surveyid)
    {
    	
    }
    
    /*--------------------------------*/
    /*--- Question level functions ---*/
    /*--------------------------------*/
    
    //show all questions associated with a particular survey
    function showquestions($surveyid) //AJAX
    {
    	
    }
    
    //show all the details of a particluar question
    function viewquestion($questionid) //AJAX
    {
    	
    }
    
    //add a new question to the current survey
    function addquestion($surveyid) //AJAX
    {
    	
    }
    
    //edit the text of a particular question
    function editquestion($questionid) //AJAX
    {
    	
    }
    
    //delete a particular question
    function deletequestion($questionid) //AJAX
    {
    	
    }
    
    /*------------------------------*/
    /*--- Branch level functions ---*/
    /*------------------------------*/
    
    //show all branches related to a particular question
    function showbranches($questionid) //AJAX
    {
    	
    }
    
    //show all conditions related to a particular branch
    function viewbranch($branchid) //AJAX
    {
    	
    }
    
    //add a new branch to the current survey
    function addbranch($questionid) //AJAX
    {
    	
    }
    
    //edit a particularbranch
    function editbranch($branchid) //AJAX
    {
    	
    }
}
?>