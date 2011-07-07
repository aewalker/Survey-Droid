<?php 
/*---------------------------------------------------------------------------*
 * controllers/answers_controller.php                                        *
 *                                                                           *
 * Contains functions that are used by the phone: push and pull to send a    *
 * subjects survey answers and pull new survey data, respectively.           *
 *---------------------------------------------------------------------------*
 * Note: status chages are not yet implemented on the phone, but will be at  *
 * some point, so the code to handle them is left in this file.  Similarly,  *
 * if at some future point, additional types of data are to be collected by  *
 * the phones, one can set the website to accept that kind of data by adding *
 * the controllers for that kind of data to the uses and models arrays.      *
 *---------------------------------------------------------------------------*/
/**
 * Controls communication between phones and the database.  Access to the push
 * and pull functions are not restricted to logged in users to allow the phones
 * to use them without being logged in.
 * 
 * @author Austin Walker
 */
class AnswersController extends AppController
{
	//for php4
	var $name = 'Answers';
	
	//this controller is associated with all the models that the phones use
	var $uses = array('Survey', 'Answer', 'Location', 'StatusChange', 'Call',
		'Subject', 'Configuration');
	var $helpers = array('Js' => 'jquery');
	var $layout = 'json';
	
	//allow anyone (eg the phones) to use push() and pull()
	var $components = array('Auth' => array
	(
		'authorize' => 'controller',
		'allowedActions' => array('push', 'pull')
	));
	
	/**
	 * Pull survey data from the database and convert to JSON.
	 */
	function pull($deviceid)
	{
		$subjectid = getID($deviceid, $message, $worked);
		if ($worked = false)
		{
			$this->set('result', $worked);
			$this->set('message', $message);
			return;
		}
		$results = array
		(
			'surveys' => array(),
			'questions' => array(),
			'choices' => array(),
			'branches' => array(),
			'conditions' => array(),
			'config' => array()
		);
		foreach (array('surveys', 'questions', 'choices', 'branches', 'conditions') as $table)
		{
			$result = $this->Survey->query("SELECT * from $table");
			foreach ($result as &$item)
			{
				//deal with special fields here
				foreach ($item[$table] as $field => &$value)
				{
					//Parse the String into a new UNIX Timestamp
					if ($field == 'created' || $field == 'modified' || $field == 'updated')
						// From http://snippets.dzone.com/posts/show/1455
						$value = strtotime($value . ' GMT');
				}
				$results[$table] = array_merge($results[$table], array($item[$table]));
			}
		}
		//config is a bit different, so do that here:
		$result = $this->Configuration->query("SELECT key,value from configurations");
		foreach ($result as $item)
		{
			foreach ($item['configuration'] as $field => $value)
			{
				$results['config'] = array_merge($results['config'], array($field => $value));
			}
		}
		$this->set('result', true);
		$this->set('results', $results);
	}
	
	/**
	 * Accepts a request continaing a JSON object with answers, locations,
	 * statuschanges, and calls and attepmts to parse that data and put it into
	 * the database.
	 */
	function push($deviceid)
	{
		//since the JSON object is in the body of the request, get the whole request text
		$info = file_get_contents('php://input');
		
		//array of modles to look for data to save as [JSON name] => [CakePHP name]
		$models = array
		(
			'answers' => 'Answer',
			'locations' => 'Location',
			'calls' => 'Call',
			'status_changes' => 'StatusChange'
		);
		
		//assuming there is some textual JSON array in $info:
		$result = true; //did the push work?  changed to false on error
		$message = NULL; //if it didn't, why not?
		$info = json_decode($info, true);
		if ($info == NULL)
		{
			$this->set('result', false);
			$this->set('message', 'Invalid JSON');
			return;
		}
		$subjectid = getID($deviceid, $message, $worked);
		if ($worked = false)
		{
			$this->set('result', $worked);
			$this->set('message', $message);
			return;
		}
		else
		{
			//now, go through the rest of the data
			foreach ($info as $table => $items)
			{
				foreach ($models as $json_name => $cake_name)
				{ //TODO this can be more efficent
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
								
								//add the deviceId to the contact_id to create an anonomyous and
								//unique number in place of the real phone number:
								if ($key == 'contact_id')
									$val = $info['deviceId'].$val;
								
								//deal with choice_ids, which uses HABTM
								if ($key == 'choice_ids')
								{
									$ids = explode(',', $val);
									if (!empty($ids))
										$toSave['Choice']['Choice'] = $ids;
								}
								else $toSave[$cake_name][$key] = $val;
							}
							//set the subject_id for all data based on deviceID
							$toSave[$cake_name]['subject_id'] = $subjectid;
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
		//finally, set the results and possibly the error message for the view
		$this->set('result', $result);
		if ($result == false) $this->set('message', $message);
	}
	
	/* some notes:
	 * 
	 * convert an array to json => use js helper: $Js->value($array);
	 * 
	 * DATETIME <=> Unix timestamp => see http://snippets.dzone.com/posts/show/1455
	 */
	
	/**
	 * Returns the subject id if one can be found for the given device id. Sets
	 * message to be the error message if an error occurs and sets worked to
	 * false if an error occurs or true if not.
	 */
	function getId($deviceid, &$message, &$worked)
	{
		$worked = true;
		$message = NULL;
		if (!isset($deviceid))
		{
			$worked = false;
			$message = 'no device id given';
			return NULL;
		}
		else
		{
			//now, make sure the given deviceId is registered to a subject
			$subjectid = $this->Subject->find('first', array
			(
				'conditions' => array('device_id' => $deviceid),
				'fields' => array('id')
			));
			$subjectid = $subjectid['Subject']['id'];
			if ($subjectid == NULL)
			{
				$worked = false;
				$message = 'invalid or unregistered device id';
			}
			return $subjectid;
		}
	}
}
?>
