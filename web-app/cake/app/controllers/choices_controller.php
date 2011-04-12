<?php 
/*****************************************************************************
 * controllers/choices_controller.php                                        *
 *                                                                           *
 * Controlls all web-end survey functions at the choice level.  All          *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class ChoicesController extends AppController
{
	//for php4
	var $name = 'Choices';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    //show all choices related to a particular question
    function showchoices($questionid)
    {
    	$this->set('results', $this->Choice->find('all', array
		(
			'conditions' => array('question_id' => $questionid),
			'fields' => array('id', 'choice_text'),
			'order' => array('choice_text')
		)));
		$this->set('questionid', $questionid);
    }
    
    //add a new choice to the current question
    function addchoice($questionid)
    {
    	$this->set('questionid', $questionid);
    	$this->Choice->create();
		if ($this->Choice->save($this->data))
        {
         	$this->Session->setFlash('New choice created!');
         	$this->set('result', true);
    	}
    }
    
    //edit a particular choice
    function editchoice($choiceid)
    {
    	if ($choiceid == NULL) return;
    	if (isset($this->data['Choice']['cancel']) && $this->data['Choice']['cancel'] == true)
    	{
    		$this->set('questionid', $this->data['Choice']['question_id']);
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Choice']['confirm'] == true)
		{
			$this->Choice->save();
			$this->Session->setFlash('Choice edited!');
			$this->set('result', true);
			$this->set('questionid', $this->data['Choice']['question_id']);
		}
		else
		{
			$result = $this->Choice->find('first', array
			(
				'conditions' => array('Choice.id' => $choiceid),
				'fields' => array('choice_text', 'question_id')
			));
			if (isset($result['Choice']))
			{
				$this->set('choice_text', $result['Choice']['choice_text']);
				$this->set('questionid', $result['Choice']['question_id']);
				$this->set('id', $choiceid);
			}
			else
			{
				$this->Session->setFlash('That choice does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
    
	//delete a particular choice
    function deletechoice($choiceid)
    {
    	if ($choiceid == NULL) return;
    	if (isset($this->data['Choice']['cancel']) && $this->data['Choice']['cancel'] == true)
    	{
    		$this->set('questionid', $this->data['Choice']['question_id']);
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Choice']['confirm'] == true)
		{
			$this->Choice->delete($choiceid);
			$this->Session->setFlash('Choice deleted!');
			$this->set('result', true);
			$this->set('questionid', $this->data['Choice']['question_id']);
		}
		else
		{
			$result = $this->Choice->find('first', array
			(
				'conditions' => array('Choice.id' => $choiceid),
				'fields' => array('choice_text', 'question_id')
			));
			if (isset($result['Choice']))
			{
				$this->set('choice_text', $result['Choice']['choice_text']);
				$this->set('id', $choiceid);
				$this->set('questionid', $result['Choice']['question_id']);
			}
			else
			{
				$this->Session->setFlash('That choice does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>