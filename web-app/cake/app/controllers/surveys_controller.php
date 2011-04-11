<?php 
/*****************************************************************************
 * controllers/surveys_controller.php                                        *
 *                                                                           *
 * Controlls all web-end survey functions at the survey level.               *
 *****************************************************************************/
class SurveysController extends AppController
{
	//for php4
	var $name = 'Surveys';
	
	var $components = array('Auth');
    var $helpers = array('Table');
    
    var $days = array('mo', 'tu', 'we', 'th', 'fr', 'sa', 'su'); //for convienence
    
    //show all surveys in a table
    function index()
    {
    	$this->set('results', $this->Survey->find('all', array
		(
			'fields' => array('id', 'name'),
			'order' => array('name')
		)));
    }
    
    //show the details of a particular survey
    function viewsurvey($surveyid)
    {
    	$this->set('surveyid', $surveyid);
    }
    
    //add a new survey
    function addsurvey()
    {
    	if($this->data['Survey']['confirm'] == true)
    	{
			$this->Survey->create();
			if ($this->Survey->save($this->data))
	        {
	         	$this->Session->setFlash('New survey created!');
	        	$this->redirect('/surveys/');
	    	}
    	}
    }
    
    //edit the name of a particular survey
    function editsurvey($surveyid)
    {
		if ($surveyid == NULL) $this->redirect('/surveys/');
		if ($this->data['Survey']['confirm'] == true)
		{
			if ($this->Survey->save($this->data))
			{
				$this->Session->setFlash('Survey edited!');
				$this->redirect('/surveys');
			}
			else
			{
				
			}
		}
		else
		{
			$result = $this->Survey->find('first', array
			(
				'conditions' => array('Survey.id' => $surveyid),
				'fields' => array_merge(array('name', 'question_id'), $this->days)
			));
			
			if (isset($result['Survey']))
			{
				$this->set('name', $result['Survey']['name']);
				$this->set('surveyid', $surveyid);
				$this->set('questionid', $result['Survey']['question_id']);
				$days_result = array();
				foreach ($this->days as $day)
				{
					$days_result[$day] = $result['Survey'][$day];
				}
				$this->set('days', $days_result);
				$this->set('testing', $result);
			}
			else
			{
				$this->Session->setFlash('That survey does not exist!  If you recieved this message after following a link, please email your system administrator.');
				$this->redirect('/surveys/');
			}
		}
    }
    
    //delete a particular survey and it's associated data
    function deletesurvey($surveyid)
    {
		if ($surveyid == NULL) $this->redirect('/surveys/');
		if ($this->data['Survey']['confirm'] == true)
		{
			$this->Survey->delete($surveyid);
			$this->Session->setFlash('Survey deleted!');
			$this->redirect('/surveys');
		}
		else
		{
			$result = $this->Survey->find('first', array
			(
				'conditions' => array('Survey.id' => $surveyid),
				'fields' => array('name')
			));
			if (isset($result['Survey']))
			{
				$this->set('name', $result['Survey']['name']);
				$this->set('id', $surveyid);
			}
			else
			{
				$this->Session->setFlash('That survey does not exist!  If you recieved this message after following a link, please email your system administrator.');
				$this->redirect('/surveys/');
			}
		}
    }
}
?>