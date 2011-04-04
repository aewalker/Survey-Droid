<?php 
/*****************************************************************************
 * controllers/subjects_controller.php                                       *
 *                                                                           *
 * Controlls functions related to the subjects (ie participants) in the      *
 * study: add/delete/edit etc.                                               *
 *****************************************************************************/
class SubjectsController extends AppController
{
	//might as well support php4
	var $name = 'Subjects';
	
	//load the Auth (ie authorization) component and the Table helper
    var $components = array('Auth');
    var $helpers = array('Table');
    
    //index function shows an overview of all the subjects matching a search,
    //or shows everyone if no search terms were entered
	function index()
	{
		$this->set('results', $this->Subject->find('all', array
		(
			'conditions' => array
			(
				'first_name LIKE' => '%'.$this->data['Subject']['first_name'].'%',
				'last_name LIKE' => '%'.$this->data['Subject']['last_name'].'%'
			),
			'fields' => array('id', 'first_name', 'last_name', 'phone_num'),
			'order' => array('last_name', 'first_name')
		)));
	}
	
	//add a new subject
	function add()
	{
		$this->Subject->create();
		if ($this->Subject->save($this->data))
	        {
	         	$this->Session->setFlash('New subject created!');
	        	$this->redirect('/subjects/');
	    	}
	}
	
	//delete an existing subject
	function delete($id = NULL)
	{
		if ($id == NULL) $this->redirect('/subjects/');
		if ($this->data['Subject']['confirm'] == true)
		{
			$this->Subject->delete($id);
			$this->Session->setFlash('Subject deleted!');
			$this->redirect('/subjects');
		}
		else
		{
			$result = $this->Subject->find('first', array
			(
				'conditions' => array('id' => $id),
				'fields' => array('first_name', 'last_name')
			));
			if (isset($result['Subject']))
			{
				$this->set('first_name', $result['Subject']['first_name']);
				$this->set('last_name', $result['Subject']['last_name']);
				$this->set('id', $id);
			}
			else
			{
				$this->Session->setFlash('That subject does not exist!  If you recieved this message after following a link, please email your system administrator.');
				$this->redirect('/subjects/');
			}
		}
	}
	
	//edit an existing subject's details
	function edit($id = NULL)
	{
		if ($id == NULL) $this->redirect('/subjects/');
		if ($this->data['Subject']['confirm'] == true)
		{
			$this->Subject->save();
			$this->Session->setFlash('Subject edited!');
			$this->redirect('/subjects');
		}
		else
		{
			$result = $this->Subject->find('first', array
			(
				'conditions' => array('id' => $id),
				'fields' => array('first_name', 'last_name', 'phone_num')
			));
			if (isset($result['Subject']))
			{
				$this->set('first_name', $result['Subject']['first_name']);
				$this->set('last_name', $result['Subject']['last_name']);
				$this->set('phone_num', $result['Subject']['phone_num']);
				$this->set('id', $id);
			}
			else
			{
				$this->Session->setFlash('That subject does not exist!  If you recieved this message after following a link, please email your system administrator.');
				$this->redirect('/subjects/');
			}
		}
	}
}
?>