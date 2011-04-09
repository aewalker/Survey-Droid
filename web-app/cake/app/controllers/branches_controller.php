<?php 
/*****************************************************************************
 * controllers/branches_controller.php                                       *
 *                                                                           *
 * Controlls all web-end survey functions at the branch level.  All          *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class BranchesController extends AppController
{
	//for php4
	var $name = 'Branches';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    //show all branches related to a particular question
    function showbranches($questionid)
    {
    	
    }
    
    //show all conditions related to a particular branch
    function viewbranch($branchid)
    {
    	
    }
    
    //add a new branch to the current question
    function addbranch($questionid)
    {
    	
    }
    
    //edit a particular branch
    function editbranch($branchid)
    {
    	
    }
    
	//delete a particular branch
    function deletebranch($branchid)
    {
    	
    }
}
?>