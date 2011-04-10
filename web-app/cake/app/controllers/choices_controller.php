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
    }
    
    //add a new choice to the current question
    function addchoice($questionid)
    {
    	$this->Choice->create();
		if ($this->Choice->save($this->data))
        {
         	$this->Session->setFlash('New choice created!');
         	$this->set('result' => true);
    	}
    }
    
    //edit a particular choice
    function editchoice($choiceid)
    {
    	if ($choiceid == NULL) return;
		if ($this->data['Choice']['confirm'] == true)
		{
			$this->Choice->save();
			$this->Session->setFlash('Choice edited!');
			$this->set('result' => true);
		}
		else
		{
			$result = $this->Choice->find('first', array
			(
				'conditions' => array('id' => $choiceid),
				'fields' => array('choice_text')
			));
			if (isset($result['Choice']))
			{
				$this->set('choice_text', $result['Choice']['choice_text']);
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
		if ($this->data['Choice']['confirm'] == true)
		{
			$this->Choice->delete($choiceid);
			$this->Session->setFlash('Choice deleted!');
			$this->set('result' => true);
		}
		else
		{
			$result = $this->Choice->find('first', array
			(
				'conditions' => array('id' => $choiceid),
				'fields' => array('choice_text')
			));
			if (isset($result['Choice']))
			{
				$this->set('choice_text', $result['Choice']['choice_text']);
				$this->set('id', $choiceid);
			}
			else
			{
				$this->Session->setFlash('That choice does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>