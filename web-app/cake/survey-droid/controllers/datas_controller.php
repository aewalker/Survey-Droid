<?php 
/*---------------------------------------------------------------------------*
 * controllers/datas_controller.php                                          *
 *                                                                           *
 * Controls data view and export.                                            *
 *---------------------------------------------------------------------------*/
/**
 * Controls data view and export.  Shows location an call log information by
 * subject, and survey answer data by survey and question.
 * 
 * @author Sema Berkiten
 */
class DatasController extends AppController
{
	var $name = 'Datas';
	//this controller is associated with all the models
	var $uses = array('Survey', 'Answer', 'Location', 'StatusChange',
		'Call', 'Question', 'Subject', 'Choice');
    
    var $helpers = array('Table', 'Tablefordata');
    
    /**
     * Show all surveys from which data can be viewed.
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
     * Show all questions for a survey with links to veiw the answers for each.
     * 
     * @param surveyid - id of the survey
     */
    function answers($surveyid)
    {
    	$this->set('results', $this->Question->find('all', array
		(
			'conditions' => array('survey_id' => $surveyid),
			'fields' => array('id', 'q_text'),
			'order' => array('q_text')
		)));
		$this->set('surveyid', $surveyid);
    }
    
    /**
     * Shows all the answers from a particular question.
     * 
     * @param questionid - id of the question whose answers should be shown
     */
	function showanswers($questionid)
    {
    	//$this->layout = 'ajax';

    	$results = $this->Answer->find('all', array
		(
			'conditions' => array('Answer.question_id' => $questionid),
			'order' => array('Answer.id')
		));
		
		//check that the question is multiple choice in order to know what fields
		//to display in the view
		$type = 'multipleChoice'; //assume multiple choice by default because it's more common
		if (count($results) == 0)
		{
			$type = NULL;
		}
		else
		{ //just look at the first answer since they are all for the same question
			if ($results[0]['Answer']['ans_value'] == NULL)
			{
				if ($results[0]['Answer']['ans_text'] != NULL)
				{
					$type = 'freeResponse';
				}
			}
			else
			{
				if ($results[0]['Answer']['ans_text'] != NULL)
				{
					$type = 'freeResponse';
				}
				else
				{
					$type = 'scale';
				}
			}
		}
		$this->set('type', $type);
		$this->set('results', $results);
		
		$qs = $this->Question->find('all', array
		(
			'conditions' => array('Question.id' => $questionid),
			'fields' => array('q_text')
		));
		foreach($qs as $q)
			$this->set('questiontext', $q["Question"]["q_text"]);
		$this->set('questionid', $questionid);

    }
    
    /**
     * Show all the subjects with links to view their location data.
     */
	function locations()
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
	
	/**
	 * Shows all the location data from a particular subject.
	 * 
	 * @param subjectid - id of the subject whose location data is shown
	 */
	function showlocations($subjectid)
	{
		$this->set('results', $this->Location->find('all', array
		(
			'conditions' => array('Location.subject_id' => $subjectid),
			'fields' => array('longitude', 'latitude', 'created'),
			'order' => array('created')
		)));
		$this->set('subjectid', $subjectid);
	}
	
	/**
	 * Shows all subjects with links to view each subjects call data.
	 */
	function calls()
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
	
	/**
	 * Shows all the call data from a particular subject.
	 * 
	 * @param subjectid - id of the subject whose call data is shown
	 */
	function showcalls($subjectid)
	{
		$this->set('results', $this->Call->find('all', array
		(
			'conditions' => array('Call.subject_id' => $subjectid),
			'fields' => array('contact_id', 'type', 'duration', 'created'),
			'order' => array('created')
		)));
		$this->set('subjectid', $subjectid);
	}
	
	/**
	 * Shows all phone status changes.
	 */
	//TODO this has not yet been implemented on the phones
	function subjectstatuses()
	{
		$this->set('results', $this->StatusChange->find('all', array
		(
			'fields' => array('subject_id', 'status', 'feature', 'created'),
			'order' => array('subject_id', 'created')
		)));
	}
    
	/**
	 * Export data in .xls format.
	 */
	function export_xls() 
	{
		$this->Data->recursive = 1;
		
		$data = $_SESSION['exportData'];
		$columns = $_SESSION['exportColumnNames'];
		$info = $_SESSION['info'];
		$pagename = $_SESSION['pagename'];
		
		$this->set('rows',$data);
		$this->set('columns',$columns);
		$this->set('pageName', $pagename);
		$this->set('info', $info);
		$this->render('export_xls','export_xls');
		
		$_SESSION['exportData'] = '';
		$_SESSION['exportColumnNames'] = '';
		$_SESSION['info'] = '';
		$_SESSION['pagename'] = '';

	}
    

}
?>
