<?php 
/*---------------------------------------------------------------------------*
 * controllers/surveys_controller.php                                        *
 *                                                                           *
 * Controlls all web-end survey functions at the survey level.               *
 *---------------------------------------------------------------------------*
 * Note: all of the survey-related classes work very similarly.  Each        *
 * follows the same pattern: all have show, edit, add, and delete functions. *
 * Because of this, it would be better to put this kind of functionality     *
 * into a component to avoid repeating some code.  However, for now, since   *
 * there are some small differences between how each of the survey objects   *
 * work, each has it's own code.                                             *
 *---------------------------------------------------------------------------*/
/**
 * Controls surveys at the high level.  Unlike the lower level survey-related
 * classes, the functions in this class are not AJAX.
 * 
 * @author Austin Walker
 * @author Sema Berkiten
 */
class SurveysController extends AppController
{
	//for php4
	var $name = 'Surveys';
	
	var $components = array('Auth');
    var $helpers = array('Table');
    
    var $days = array('mo', 'tu', 'we', 'th', 'fr', 'sa', 'su'); //for convienence
    
    /**
     * Display all surveys.
     */
    function index()
    {
    	$this->set('results', $this->Survey->find('all', array
		(
			'fields' => array('id', 'name'),
			'order' => array('name')
		)));
    }
    
    /**
     * Show the details of a particular survey and edit it's internal
     * question/choice/branch/condition structure.
     * 
     * @param surveyid - id of the survey to show
     */
    function viewsurvey($surveyid)
    {
    	$result = $this->Survey->find('first', array
    	(
    		'fields' => array('name'),
    		'conditions' => array('Survey.id' => $surveyid))
    	);
    	$this->set('surveyname', $result['Survey']['name']);
    	$this->set('surveyid', $surveyid);
    }
    
    /**
     * Add a new survey.
     */
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
    
    /**
     * Edit the name, first question, and times of a particular survey.
     * 
     * @param surveyid - id of the survey to edit
     */
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
    
    /**
     * Delete a particular survey and it's associated data.
     * 
     * @param surveyid - id of the survey to delete
     */
    function deletesurvey($surveyid)
    {
		if ($surveyid == NULL) $this->redirect('/surveys/');
		if ($this->data['Survey']['confirm'] == true)
		{
			//set $cascade = true to delete all questions, etc. that in in this survey.
			$this->Survey->delete($surveyid, true);
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