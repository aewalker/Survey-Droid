<?php 
/*****************************************************************************
 * controllers/answers_controller.php                                        *
 *                                                                           *
 * Contains functions that are used by the phone: push and pull to send a    *
 * subjects survey answers and pull new survey data, respectively.           *
 *****************************************************************************/
class AnswersController extends AppController
{
	//for php4
	var $name = 'Answers';
	
	//this controller is associated with all the models that the phones use
	var $uses = array('Survey', 'Answer', 'Location', 'StatusChange', 'Call', 'Subject');
	var $helpers = array('Js' => 'jquery');
	var $layout = 'json';
	
	//allow anyone (eg the phones) to use push() and pull()
	var $components = array('Auth' => array
	(
		'authorize' => 'controller',
		'allowedActions' => array('push', 'pull')
	));
	
	//pull survey data (and descendants) from the database
	function pull()
	{
		$results = array
		(
			'surveys' => array(),
			'questions' => array(),
			'choices' => array(),
			'branches' => array(),
			'conditions' => array()
		);
		foreach (array('surveys', 'questions', 'choices', 'branches', 'conditions') as $table)
		{
			$result = $this->Survey->query("SELECT * from $table");
			foreach ($result as &$item)
			{
				// Parse the String into a new UNIX Timestamp
				foreach ($item[$table] as $field => &$value)
				{
					if ($field == 'created' || $field == 'modified' || $field == 'updated')
						// From http://snippets.dzone.com/posts/show/1455
						$value = strtotime($value . ' GMT');
				}
				
				$results[$table] = array_merge($results[$table], array($item[$table]));
			}
		}
		$this->set('results', $results);
	}
	
	//push answers, locations, statuschanges, and calls to the database
	function push()
	{
		$info = $this->data['data'];
		//$info = '{"deviceId":"lololol","surveys":[{"field":"value"},{"field":"value"}],'.
		//	'"answers":[{"question_id":1,"choice_id":1,"created":'.time().'}, {"question_id":2,"choice_id":3,"created":'.time().'}]}';
		
		//array of modles to look for data to save as [JSON name] => [CakePHP name]
		$models = array
		(
			'answers' => 'Answer',
			'locations' => 'Location',
			'calls' => 'Call',
			'status_changes' => 'StatusChange'
		);
		
		//assuming there is some textual JSON array in $info:
		$result = true; //did the push work?
		$message = NULL; //if it didn't, why not?
		$info = json_decode($info, true);
		if ($info == NULL)
		{
			$result = false;
			$message = 'Invalid JSON';
		}
		else
		{
			//first, make sure that there is a deviceId
			$subjectid = NULL;
			if (!isset($info['deviceId']))
			{
				$result = false;
				$message = 'no device id given';
			}
			else
			{
				/******************************/
				//testing only!!!!
				$info['deviceId'] = 'phone1';
				/******************************/
				
				//now, make sure the given deviceId is registered to a subject
				$subjectid = $this->Subject->find('first', array
				(
					'conditions' => array('device_id' => $info['deviceId']),
					'fields' => array('id')
				));
				if ($subjectid == NULL)
				{
					$result = false;
					$message = 'invalid device id';
				}
				else
				{
					//now, go through the rest of the data
					foreach ($info as $table => $items)
					{
						foreach ($models as $json_name => $cake_name)
						{
							if ($table == $json_name)
							{
								foreach ($items as $item)
								{
									$toSave = array();
									foreach ($item as $key => $val)
									{
										//turn Unix timestamps into MySQL DATETIME format
										if ($key == 'created' || $key == 'modified' || $key == 'updated')
											// From http://snippets.dzone.com/posts/show/1455
											$val = gmdate('Y-m-d H:i:s', $val);
										
										//add the deviceId to the contact_id to create an anonomyous number
										//in place of the phone number:
										if ($key == 'contact_id')
											$val = $info['deviceId'].$val;
										
										$toSave[$cake_name][$key] = $val;
									}
									//set the subject_id for all data based on deviceID
									$toSave[$cake_name]['subject_id'] = $subjectid;
									print_r($toSave);
									$this->$cake_name->create();
									if (!$this->$cake_name->save($toSave))
									{
										$result = false;
										$message = $this->$cake_name->validationErrors;
									}
								}
							}
						}
					}
				}
			}
		}
		$this->set('result', $result);
		if ($result == false) $this->set('message', $message);
	}
	
	/* some notes:
	 * 
	 * convert an array to json => use js helper: $Js->value($array);
	 * 
	 * DATETIME <=> Unix timestamp => see http://snippets.dzone.com/posts/show/1455
	 */
}
?>