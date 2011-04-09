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
    
    //show all conditions related to a particular branch
    function showconditions($branchid)
    {
    	
    }
    
    //add a new condition to the current branch
    function addcondition($branchid)
    {
    	
    }
    
    //edit a particular condition
    function editcondition($conditionid)
    {
    	
    }
    
	//delete a particular condition
    function deletecondition($conditionid)
    {
    	
    }
}
?>