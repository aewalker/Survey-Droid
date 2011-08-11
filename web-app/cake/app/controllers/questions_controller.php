<?php 
/*---------------------------------------------------------------------------*
 * controllers/questions_controller.php                                      *
 *                                                                           *
 * Controlls all web-end survey functions at the question level.  All        *
 * functions are ment to be AJAX.                                            *
 *---------------------------------------------------------------------------*/
/**
 * Controls the question level of surveys.  See {@link SurveysController} for
 * more information.
 * 
 * @author Austin Walker
 * @author Sema Berkiten
 */
class QuestionsController extends AppController
{
	//for php4
	var $name = 'Questions';
	
	var $components = array('Auth');
    var $helpers = array('Table', 'Js' => 'jquery');
    
    var $layout = 'ajax';
    
    /**
     * Show all questions related to a particular survey.
     * 
     * @param surveyid - id of the survey whose questions are to be shown
     */
    function showquestions($surveyid)
    {
    	$this->set('results', $this->Question->find('all', array
		(
			'conditions' => array('survey_id' => $surveyid),
			'fields' => array('id', 'q_text'),
			'order' => array('id')
		)));
		$this->set('surveyid', $surveyid);
    }
    
    /**
     * Add a new question to a survey.
     * 
     * @param surveyid - id of the Question to which a branch should be added
     */
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
			//first do the base64 transform
			if (!empty($this->data['Question']['q_img_low']['tmp_name']))
			{
                // var_dump($this->data);
                // echo empty($this->data['Question']['q_img_low']);
				$file = $this->data['Question']['q_img_low']['tmp_name'];
				$this->data['Question']['q_img_low'] =
					base64_encode(fread(fopen($file, 'r'), filesize($file)));
			} else {
			    $this->data['Question']['q_img_low'] = "";
			}
			if (!empty($this->data['Question']['q_img_high']['tmp_name']))
			{
				$file = $this->data['Question']['q_img_high']['tmp_name'];
				$this->data['Question']['q_img_high'] =
					base64_encode(fread(fopen($file, 'r'), filesize($file)));
			} else {
			    $this->data['Question']['q_img_high'] = "";
        	}
			
			//then save
	    	$this->Question->create();
			if ($this->Question->save($this->data))
	        {
	         	$this->Session->setFlash('New question created!');
	    	}
	    	else
	    	{
	    		$this->Session->setFlash('There were errors');
	    	}
	        $this->redirect('/surveys/viewsurvey/'.$this->data['Question']['survey_id']);
		}		
    }
    
    /**
     * Edit the text of a particular question.
     * 
     * @param questionid - the id of the question to edit
     */
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
			//first do the base64 transform
			if (!empty($this->data['Question']['q_img_low']))
			{
				$file = $this->data['Question']['q_img_low']['tmp_name'];
				$this->data['Question']['q_img_low'] =
					base64_encode(fread(fopen($file, 'r'), filesize($file)));
			}
			if (!empty($this->data['Question']['q_img_high']))
			{
				$file = $this->data['Question']['q_img_high']['tmp_name'];
				$this->data['Question']['q_img_high'] =
					base64_encode(fread(fopen($file, 'r'), filesize($file)));
			}
			
			if ($this->Question->save($this->data))
			{
				$this->Session->setFlash('Question edited!');
			}
			else
			{
				$this->Session->setFlash('There were errors');
			}
			$this->redirect('/surveys/viewsurvey/'.$this->data['Question']['survey_id']);
			return;
		}
		
		$result = $this->Question->find('first', array
		(
			'conditions' => array('Question.id' => $questionid)
		));
		if (isset($result['Question']))
		{
			$this->set('q_type', $result['Question']['q_type']);
			$this->set('q_text_low', $result['Question']['q_text_low']);
			$this->set('q_text_high', $result['Question']['q_text_high']);
			$this->set('q_img_low', $result['Question']['q_img_low']);
			$this->set('q_img_high', $result['Question']['q_img_high']);
			$this->set('q_text', $result['Question']['q_text']);
			$this->set('questionid', $questionid);
			$this->set('surveyid', $result['Question']['survey_id']);
		}
		else
		{
			$this->Session->setFlash('That question does not exist!  If you recieved this message after following a link, please email your system administrator.');
		}
    }
    
    /**
	 * Delete a particular question.
	 * 
	 * @param questionid - id of the question to delete
	 */
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