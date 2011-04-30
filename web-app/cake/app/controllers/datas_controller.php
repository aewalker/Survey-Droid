<?php 
/*****************************************************************************
 * controllers/datas_controller.php                                          *
 *                                                                           *
 * Controlls data view and export.                                           *
 *****************************************************************************/
class DatasController extends AppController
{
	var $name = 'Datas';
	//this controller is associated with all the models
	var $uses = array('Survey', 'Answer', 'Location', 'StatusChange', 'Call', 'Question', 'Subject', 'Choice');
    
    var $helpers = array('Table', 'Tablefordata');
    
    function index()
    {
    	$this->set('results', $this->Survey->find('all', array
		(
			'fields' => array('id', 'name'),
			'order' => array('name')
		)));
    }
    
    function viewdataoptions($surveyid)
    {
    	$this->set('surveyid', $surveyid);
    }
    
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
    
	function showanswers($questionid)
    {
    	//$this->layout = 'ajax';

    	$results = $this->Answer->find('all', array
		(
			'conditions' => array('Answer.question_id' => $questionid),
			'fields' => array('Answer.choice_id', 'Choice.choice_text', 
					'Answer.ans_text','Answer.created', 'Answer.subject_id'),
			'order' => array('Answer.id')
		));
		
		//check that the question is multiple choice in order to know what fields
		//to display in the view
		$multipleChoice = true; //assume multiple choice by default because it's more common
		foreach ($results as $result)
		{
			if (!$result['Answer']['choice_id'])
			{
				$multipleChoice = false;
			}
		}
		$this->set('multipleChoice', $multipleChoice);
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
	
	function subjectstatuses()
	{
		$this->set('results', $this->StatusChange->find('all', array
		(
			'fields' => array('subject_id', 'status', 'feature', 'created'),
			'order' => array('subject_id', 'created')
		)));
	}
    
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
     