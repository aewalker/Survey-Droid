<?php 
/*---------------------------------------------------------------------------*
 * controllers/subjects_controller.php                                       *
 *                                                                           *
 * Controlls functions related to the subjects (ie participants) in the      *
 * study: add/delete/edit etc.                                               *
 *---------------------------------------------------------------------------*/
/**
 * Controls subjects (ie phone users).  In order for a phone to work with the
 * site, it must be registered (it's device id added) to a subject.
 * 
 * @author Austin Walker
 */
class SubjectsController extends AppController
{
	//might as well support php4
	var $name = 'Subjects';
	
	//load the Auth (ie authorization) component and the Table helper
    var $components = array('Auth');
    var $helpers = array('Table');
    
    /**
     * Shows an overview of all the subjects matching a search, or shows
     * everyone if no search terms were entered.
     */
	function index()
	{
		$this->set('results', $this->Subject->find('all', array
		(
			'conditions' => array //allow search by any of the 4 subject fields
			(
				'first_name LIKE' => '%'.$this->data['Subject']['first_name'].'%',
				'last_name LIKE' => '%'.$this->data['Subject']['last_name'].'%',
				'phone_num LIKE' => '%'.$this->data['Subject']['phone_num'].'%',
				'device_id LIKE' => '%'.$this->data['Subject']['device_id'].'%'
			),
			'fields' => array('id', 'first_name', 'last_name', 'phone_num', 'device_id'),
			'order' => array('last_name', 'first_name')
		)));
	}
	
	/**
	 * Add a new subject.
	 */
	function add()
	{
		$this->Subject->create();
		if ($this->Subject->save($this->data))
        {
         	$this->Session->setFlash('New subject created!');
        	$this->redirect('/subjects/');
    	}
	}
	
	/**
	 * Delete an existing subject.
	 * 
	 * @param id - id of the subject to delete
	 */
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
	
	/**
	 * Edit an existing subject's details.
	 * 
	 * @param id - id of the subject whose info is to be edited
	 */
	function edit($id = NULL)
	{

		if ($id == NULL) $this->redirect('/subjects/');
		if ($this->data['Subject']['confirm'] == true)
		{
			$this->Subject->save($this->data);
			$this->Session->setFlash('Subject edited!');
			$this->redirect('/subjects');
		}
		else
		{
			$result = $this->Subject->find('first', array
			(
				'conditions' => array('id' => $id),
				'fields' => array('first_name', 'last_name', 'phone_num', 'device_id')
			));
			if (isset($result['Subject']))
			{
				$this->set('first_name', $result['Subject']['first_name']);
				$this->set('last_name', $result['Subject']['last_name']);
				$this->set('phone_num', $result['Subject']['phone_num']);
				$this->set('device_id', $result['Subject']['device_id']);
				$this->set('id', $id);
			}
			else
			{
				$this->Session->setFlash('That subject does not exist!  If you recieved this message after following a link, please email your system administrator.');
				$this->redirect('/subjects/');
			}
		}
	}
	
	/**
	 * View subject's information.
	 * 
	 * @param subjectid - id of the subject whose information is to be shown
	 */
	function view($subjectid)
	{
		$result = $this->Subject->find('first', array
		(
			'conditions' => array('id' => $subjectid),
			'fields' => array('first_name', 'last_name', 'phone_num', 'device_id')
		));
		if (isset($result['Subject']))
		{
			$this->set('first_name', $result['Subject']['first_name']);
			$this->set('last_name', $result['Subject']['last_name']);
			$this->set('phone_num', $result['Subject']['phone_num']);
			$this->set('device_id', $result['Subject']['device_id']);
		}
		else
			echo 'An error has occured!';
	}

    /** ------- Restful API begins here !  -------- **/
    /**
     * TODO: Refactor this so I don't have to keep on repeating
     * $this->autoRender = false and $this->data = json_decode(file_get_contents('php//input'), true)
     */
    function rest_index() {
        $this->autoRender = false;
        $subjects= $this->Subject->find('all', array(
            'recursive' => 0
        ));
        e(json_encode($subjects));
    }

    /** Create */
    function rest_create() {
        $this->autoRender = false;
        $this->data = json_decode(file_get_contents('php://input'), true);
        if (!empty($this->data)) {
            unset($this->data['Subject']['id']); // disallow client-assigned id
            if ($this->Subject->save($this->data)) {;
                $this->header('HTTP/1.1 201 Created');
                e(json_encode($this->Subject->read())); // TODO: read() returns associated models, which is unintended
                return;
            }
        }
        $this->header('HTTP/1.1 400 Bad Request');
    }

    /** Read */
    function rest_read($id) {
        $this->autoRender = false;
        $subject= $this->Subject->find('first', array(
            'conditions' => array('Subject.id' => $id),
            'recursive' => 0
        ));
        if ($subject) {
            e(json_encode($subject));
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }

    /** Update */
    function rest_update($id) {
        $this->autoRender = false;
        if ($this->Subject->read(null, $id)) {
            $this->data = json_decode(file_get_contents('php://input'), true);
            if (!empty($this->data)) {
                unset($this->data['Subject']['id']); // disallow client-assigned id
                if ($this->Subject->save($this->data)) {;
                    e(json_encode($this->Subject->read()));
                    return;
                }
            }
            $this->header('HTTP/1.1 400 Bad Request');
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }

    /** Delete */
    function rest_delete($id) {
        $this->autoRender = false;
        if($this->Subject->delete($id)) {
            $this->header('HTTP/1.1 204 No Content');
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }
}
?>