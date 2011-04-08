<?php 
/*****************************************************************************
 * controllers/conditions_controller.php                                     *
 *                                                                           *
 * Controlls all web-end survey functions at the ccondition level.  All      *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class ConditionsController extends AppController
{
	//for php4
	var $name = 'Conditions';
	
	var $components = array('Auth');
    var $helpers = array('Table');
    
    //show all branches related to a particular question
    function showbranches($questionid)
    {
    	
    }
    
    //add a new branch to the current survey
    function addbranch($questionid)
    {
    	
    }
    
    //edit a particularbranch
    function editbranch($branchid)
    {
    	
    }
    
	//delete a particular branch
    function deletebranch($branchid)
    {
    	
    }
}
?>