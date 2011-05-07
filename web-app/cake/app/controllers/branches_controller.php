<?php 
/*---------------------------------------------------------------------------*
 * controllers/branches_controller.php                                       *
 *                                                                           *
 * Controlls all web-end survey functions at the branch level.  All          *
 * functions are ment to be AJAX.                                            *
 *---------------------------------------------------------------------------*/
/**
 * Controls the Branches level of surveys.  See {@link SurveysController} for
 * more information.
 * 
 * @author Austin Walker
 * @author Sema Berkiten
 */
class BranchesController extends AppController
{
	//for php4
	var $name = 'Branches';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    /**
     * Show all branches related to a particular question.
     * 
     * @param questionid - id of the Question whose branches are to be shown
     */
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
    
    /**
     * Add a new branch to a question.
     * 
     * @param questionid - id of the Question to which a branch should be added
     */
    function addbranch($questionid)
    {
    	$this->set('questionid', $questionid);
    	if (isset($this->data['Branch']['cancel']) && $this->data['Branch']['cancel'] == true)
    	{
   			$this->set('result', true);
   			return;
    	}
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
    
    /**
     * Edit the question a particular branch points to.
     * 
     * @param branchid - the id of the branch to edit
     */
    function editbranch($branchid)
    {
    	$result = $this->Branch->find('first', array
    	(
    		'conditions' => array('Branch.id' => $branchid),
    		'fields' => array('question_id')
    	));
    	$this->set('questionid', $result['Branch']['question_id']);
    	if (isset($this->data['Branch']['cancel']) && $this->data['Branch']['cancel'] == true)
    	{
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Branch']['confirm'] == true)
		{
			if ($this->Branch->save($this->data))
			{
				$this->Session->setFlash('Branch edited!');
				$this->set('result', true);
				return;
			}
			$this->set('result', false);
		}
		$result = $this->Branch->find('first', array
		(
			'conditions' => array('Branch.id' => $branchid),
			'fields' => array('next_q', 'question_id')
		));
		if (isset($result['Branch']))
		{
			$this->set('next_q', $result['Branch']['next_q']);
			$this->set('branchid', $branchid);
			$this->set('questionid', $result['Branch']['question_id']);
		}
		else
		{
			$this->Session->setFlash('That branch does not exist!  If you recieved this message after following a link, please email your system administrator.');
		}
    }
    
	/**
	 * Delete a particular branch.
	 * 
	 * @param branchid - id of the branch to delete
	 */
    function deletebranch($branchid)
    {
    	if ($branchid == NULL) return;
   		if (isset($this->data['Branch']['cancel']) && $this->data['Branch']['cancel'] == true)
    	{
    		$this->set('questionid', $this->data['Branch']['question_id']);
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Branch']['confirm'] == true)
		{
			//want to delete all the conditions associated with a branch, so use $cascade = true
			$this->Branch->delete($branchid, true);
			$this->Session->setFlash('Branch deleted!');
			$this->set('questionid', $this->data['Branch']['question_id']);
			$this->set('result', true);
		}
		else
		{
			$result = $this->Branch->find('first', array
			(
				'conditions' => array('Branch.id' => $branchid),
				'fields' => array('next_q', 'question_id')
			));
			if (isset($result['Branch']))
			{
				$this->set('next_q', $result['Branch']['next_q']);
				$this->set('id', $branchid);
				$this->set('questionid', $result['Branch']['question_id']);
			}
			else
			{
				$this->Session->setFlash('That branch does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>