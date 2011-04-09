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
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    //show all conditions related to a particular branch
    function showconditions($branchid)
    {
    	$this->set('results', $this->Condition->find('all', array
		(
			'conditions' => array('branch_id' => $branchid),
			'fields' => array('id', 'question_id', 'choice_id'),
			'order' => array('id')
		)));
    }
    
    //add a new condition to the current branch
    function addcondition($branchid)
    {
    	$this->Condition->create();
		if ($this->Condition->save($this->data))
        {
         	$this->Session->setFlash('New condition created!');
         	$this->set('result' => true);
    	}
    }
    
    //edit a particular condition
    function editcondition($conditionid)
    {
    	if ($conditionid == NULL) return;
		if ($this->data['Condition']['confirm'] == true)
		{
			$this->Condition->save();
			$this->Session->setFlash('Condition edited!');
			$this->set('result' => true);
		}
		else
		{
			$result = $this->Condition->find('first', array
			(
				'conditions' => array('id' => $conditionid),
				'fields' => array('question_id', 'choice_id')
			));
			if (isset($result['Condition']))
			{
				$this->set('question_id', $result['Condition']['question_id']);
				$this->set('choice_id', $result['Condition']['choice_id']);
				$this->set('id', $conditionid);
			}
			else
			{
				$this->Session->setFlash('That condition does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
    
	//delete a particular condition
    function deletecondition($conditionid)
    {
    	if ($conditionid == NULL) return;
		if ($this->data['Condition']['confirm'] == true)
		{
			$this->Condition->delete($conditionid);
			$this->Session->setFlash('Condition deleted!');
			$this->set('result' => true);
		}
		else
		{
			$result = $this->Condition->find('first', array
			(
				'conditions' => array('id' => $conditionid),
				'fields' => array('question_id', 'choice_id')
			));
			if (isset($result['Condition']))
			{
				$this->set('question_id', $result['Condition']['question_id']);
				$this->set('choice_id', $result['Condition']['choice_id']);
				$this->set('id', $conditionid);
			}
			else
			{
				$this->Session->setFlash('That condition does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>