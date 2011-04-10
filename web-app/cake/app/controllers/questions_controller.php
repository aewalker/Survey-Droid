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
    
    //var $layout = 'ajax';
    
    //show all questions associated with a particular survey
    function showquestions($surveyid)
    {
    	$this->set('results', $this->Question->find('all', array
		(
			'conditions' => array('survey_id' => $surveyid),
			'fields' => array('id', 'q_text'),
			'order' => array('q_text')
		)));
		$this->set('surveyid', $surveyid);
    }
    
    //show all the details of a particluar question
    function viewquestion($questionid)
    {
    	//TODO I don't think this is really needed, have to ask Sema about it
    }
    
    //add a new question to the current survey
    function addquestion($surveyid)
    {
    	$this->set('surveyid', $surveyid);
    	if ($this->data['Question']['confirm'] == true)
		{
	    	$this->Question->create();
			if ($this->Question->save($this->data))
	        {
	         	$this->Session->setFlash('New question created!');
	         	$this->redirect('showquestions/'.$surveyid);
	         	$this->set('result', true);
	    	}
		}		
    }
    
    //edit the text of a particular question
    function editquestion($questionid)
    {
    	if ($questionid == NULL) return;
		if ($this->data['Question']['confirm'] == true)
		{
			$this->Question->save();
			$this->Session->setFlash('Question edited!');
			$this->set('result', true);
		}
		else
		{
			$result = $this->Question->find('first', array
			(
				'conditions' => array('id' => $questionid),
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
    }
    
    //delete a particular question
    function deletequestion($questionid)
    {
    	if ($questionid == NULL) return;
		if ($this->data['Question']['confirm'] == true)
		{
			$this->Question->delete($questionid);
			$this->Session->setFlash('Question deleted!');
			$this->set('result', true);
		}
		else
		{
			$result = $this->Question->find('first', array
			(
				'conditions' => array('id' => $questionid),
				'fields' => array('q_text')
			));
			if (isset($result['Question']))
			{
				$this->set('q_text', $result['Question']['q_text']);
				$this->set('id', $questionid);
			}
			else
			{
				$this->Session->setFlash('That question does not exist!  If you recieved this message after following a link, please email your system administrator.');
			}
		}
    }
}
?>