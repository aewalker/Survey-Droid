<?php
/*---------------------------------------------------------------------------*
 * models/survey.php                                                         *
 *                                                                           *
 * Model for surveys.                                                        *
 *---------------------------------------------------------------------------*/
/**
 * Model for the surveys presented to phone users.
 * 
 * @author Austin Walker
 */
class Survey extends AppModel
{
	//for php4
	var $name = 'Survey';
	
	//the survey has a first question; to Cake, this means the survey "belongs
	//to" that question
	var $belongsTo = 'Question';
	
	var $hasMany = 'Question';
	
	var $validate = array
	(
		'name' => array
		(
			'minLength' => array
			(
				'rule' => array('minLength', 1),
				'message' => 'Please provide a survey name'
			),
			'maxLength' => array
			(
				'rule' => array('maxLength', 255),
				'message' => 'Survey name cannot be longer than 255 characters'
			)
		),
		'subject_variables' => array
		(
		    'rule' => array('checkSubjectVariables'),
		    'message' => 'Subject variables must be in valid json format and contains valid device ids'
		)
		//TODO Probably should add validation for days of week...
	);
	
	function checkSubjectVariables($args) {
	    if (empty($args['subject_variables']))
	        return true;
        $subjectVariables = json_decode($args['subject_variables']);
        if ($subjectVariables == null)
            return false;

        App::import('Model', "Subject");
	    $Subject = new Subject();
        foreach ($subjectVariables as $deviceId => $variable)  {
            $deviceIdCount = $Subject->find('count', array
            (
                'conditions' => array('device_id' => $deviceId)
            ));
            if ($deviceIdCount == 0)
                return false;
        }
        
        return true;
	}
}
?>