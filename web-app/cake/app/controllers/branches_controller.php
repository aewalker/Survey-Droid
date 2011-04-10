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
    	$this->set('results', $this->Branch->find('all', array
		(
			'conditions' => array('question_id' => $questionid),
			'fields' => array('id', 'next_q'),
			'order' => array('id')
		)));
		$this->set('questionid', $questionid);
    }
    
    //show all conditions related to a particular branch
    function viewbranch($branchid)
    {
    	//TODO I don't think this is really needed, have to ask Sema about it
    }
    
    //add a new branch to the current question
    function addbranch($questionid)
    {
    	$this->set('questionid', $questionid);
    	if ($this->data['Branch']['confirm'] == true)
		{
	    	$this->Branch->create();
			if ($this->Branch->save($this->data))
	        {
	         	$this->Session->setFlash('New branch created!');
	         	$this->set('result', true);
	    	}
		}
    }
    
    //edit a particular branch
    function editbranch($branchid)
    {
    	if ($branchid == NULL) return;
		if ($this->data['Branch']['confirm'] == true)
		{
			$this->Branch->save();
			$this->Session->setFlash('Branch edited!');
			$this->set('result', true);
		}
		else
		{
			$result = $this->Branch->find('first', array
			(
				'conditions' => array('id' => $branchid),
				'fields' => array('next_q')
			));
			if (isset($result['Branch']))
			{
				$this->set('next_q', $result['Branch']['next_q']);
				$this->set('id', $branchid);
			}
			else
			{
				$this->Session->setFlash('That branch does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
    
	//delete a particular branch
    function deletebranch($branchid)
    {
    	if ($branchid == NULL) return;
		if ($this->data['Branch']['confirm'] == true)
		{
			$this->Branch->delete($branchid);
			$this->Session->setFlash('Branch deleted!');
			$this->set('result', true);
		}
		else
		{
			$result = $this->Branch->find('first', array
			(
				'conditions' => array('id' => $branchid),
				'fields' => array('next_q')
			));
			if (isset($result['Branch']))
			{
				$this->set('next_q', $result['Branch']['next_q']);
				$this->set('id', $branchid);
			}
			else
			{
				$this->Session->setFlash('That branch does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>