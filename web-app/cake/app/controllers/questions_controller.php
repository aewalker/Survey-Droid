<?php 
/*****************************************************************************
 * controllers/questions_controller.php                                      *
 *                                                                           *
 * Controlls all web-end survey functions at the question level.  All        *
 * functions are ment to be AJAX.                                            *
 *****************************************************************************/
class QuestionsController extends AppController
{
	//for php4
	var $name = 'Questions';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    //show all questions associated with a particular survey
    function showquestions($surveyid = NULL)
    {
    	$this->set('results', $this->Question->find('all', array
		(
			'conditions' => array('survey_id' => $surveyid),
			'fields' => array('id', 'q_text'),
			'order' => array('q_text')
		)));
		$this->set('surveyid', $surveyid);
    }
    
    //add a new question to the current survey
    function addquestion($surveyid)
    {
    	$this->set('surveyid', $surveyid);
    	if (isset($this->data['Question']['cancel']) && $this->data['Question']['cancel'] == true)
    	{
   			$this->set('result', true);
   			return;
    	}
    	if ($this->data['Question']['confirm'] == true)
		{
	    	$this->Question->create();
			if ($this->Question->save($this->data))
	        {
	         	$this->Session->setFlash('New question created!');
	         	$this->set('result', true);
	    	}
		}		
    }
    
    //edit the text of a particular question
    function editquestion($questionid)
    {
    	$result = $this->Question->find('first', array
    	(
    		'conditions' => array('Question.id' => $questionid),
    		'fields' => array('survey_id')
    	));
    	$this->set('surveyid', $result['Question']['survey_id']);
    	if (isset($this->data['Question']['cancel']) && $this->data['Question']['cancel'] == true)
    	{
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Question']['confirm'] == true)
		{
			if ($this->Question->save($this->data))
			{
				$this->Session->setFlash('Question edited!');
				$this->set('result', true);
				return;
			}
			$this->set('result', false);
		}
		
		$result = $this->Question->find('first', array
		(
			'conditions' => array('Question.id' => $questionid),
			'fields' => array('q_text','survey_id')
		));
		if (isset($result['Question']))
		{
			$this->set('q_text', $result['Question']['q_text']);
			$this->set('questionid', $questionid);
			$this->set('surveyid', $result['Question']['survey_id']);
		}
		else
		{
			$this->Session->setFlash('That question does not exist!  If you recieved this message after following a link, please email your system administrator.');
		}
    }
    
    //delete a particular question
    function deletequestion($questionid)
    {
    	$result = $this->Question->find('first', array
    	(
    		'conditions' => array('Question.id' => $questionid),
    		'fields' => array('survey_id')
    	));
    	$this->set('surveyid', $result['Question']['survey_id']);
    	if (isset($this->data['Question']['cancel']) && $this->data['Question']['cancel'] == true)
    	{
    		$this->set('result', true);
    		return;
    	}
		if ($this->data['Question']['confirm'] == true)
		{
			//want to delete all things (eg choices) that depend on this question, so set $cascade = true
			$this->Question->delete($questionid, true);
			$this->Session->setFlash('Question deleted!');
			$this->set('result', true);
		}
		else
		{
			$result = $this->Question->find('first', array
			(
				'conditions' => array('Question.id' => $questionid),
				'fields' => array('Question.id, survey_id')
			));
			if (isset($result['Question']))
			{
				$this->set('survey_id', $result['Question']['survey_id']);
				$this->set('questionid', $questionid);
			}
			else
			{
				$this->Session->setFlash('That question does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>