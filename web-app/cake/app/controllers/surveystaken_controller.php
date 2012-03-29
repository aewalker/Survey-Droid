<?php 
/*---------------------------------------------------------------------------*
 * controllers/answers_controller.php                                      *
 *                                                                           *
 * Controlls all web-end survey functions at the question level.  All        *
 * functions are ment to be AJAX.                                            *
 *---------------------------------------------------------------------------*/
/**
 * output answers
 * 
 * @author Tony Xiao
 */
App::import('Controller', 'Rest');
class SurveysTakenController extends RestController
{
	//for php4
	var $name = 'SurveysTaken';
	var $components = array('Auth');

    function rest_index() {
        $this->autoRender = false;
        $this->header('Content-Type: application/json');
        $modelClass = $this->modelClass;
        // add any applicable filters
        $conditions = array();
        if (array_key_exists('filter', $this->params['url'])) {
            $filters = json_decode($this->params['url']['filter'], true);
            foreach ($filters as $filter)
                if (array_key_exists($filter['property'], $this->$modelClass->_schema))
                    $conditions[$modelClass.'.'.$filter['property']] = $filter['value'];
        }

        $models = $this->$modelClass->find('all', array(
            'recursive' => -1,
            'conditions' => $conditions,
            'limit' => 300
        ));
        e(json_encode(standardize($models, $modelClass)));
    }

    /** csv dump */
    function dump() {
        $this->autoRender = false;
        ini_set('max_execution_time', 600); //increase max_execution_time to 10 min if data set is very large
        
        $modelClass = $this->modelClass;
        $filename = $modelClass . "_dump_".date("Y.m.d").".csv";

        header('Content-type: application/csv');
        header('Content-Disposition: attachment; filename="'.$filename.'"');

        // custom stuff
        $csv_file = fopen('php://output', 'w');
        $headers = array('Time', 'Subject Id', 'Survey Id', 'Thermometer %', 'Status');
        fputcsv($csv_file, $headers, ',', '"');

        $total = $this->$modelClass->find('count');
        $increment = 100;

        for ($offset = 0; $offset<$total; $offset+=$increment) {
            $models = $this->$modelClass->find('all', array(
                'recursive' => 1,
                'order' => 'SurveysTaken.created DESC',
                'offset' => $offset,
                'limit' => $increment
            ));
            partial_dump($csv_file, $models);
        }
    
        fclose($csv_file);
    }
}

function partial_dump($csv_file, $models) {
    foreach($models as $item) {
        $row = array();
        $row[] = $item['SurveysTaken']['created']; // Time Answered

        $row[] = $item['SurveysTaken']['subject_id']; // Subject Id
        $row[] = $item['SurveysTaken']['survey_id']; // Survey Id

        if ($item['SurveysTaken']['rate']) // Thermometer %
            $row[] = $item['SurveysTaken']['rate'] . "%";
        else
            $row[] = "";

        switch ($item['SurveysTaken']['status']) { // Status
            case 0:  $row[] = 'Surveys Disabled Locally'; break;
            case 1:  $row[] = 'Surveys Disabled by Server'; break;
            case 2:  $row[] = 'User Initiated Survey Finished'; break;
            case 3:  $row[] = 'User Initiated Survey Unfinished'; break;
            case 4:  $row[] = 'Scheduled Survey Finished'; break;
            case 5:  $row[] = 'Scheduled Survey Unfinished'; break;
            case 6:  $row[] = 'Scheduled Survey Dismissed'; break;
            case 7:  $row[] = 'Scheduled Survey Ignored'; break;
            case 8:  $row[] = 'Random Survey Finished'; break;
            case 9:  $row[] = 'Random Survey Unfinished'; break;
            case 10: $row[] = 'Random Survey Dismissed'; break;
            case 11: $row[] = 'Random Survey Ignored'; break;
            case 12: $row[] = 'Call Initiated Survey Finished'; break;
            case 13: $row[] = 'Call Initiated Survey Unfinished'; break;
            case 14: $row[] = 'Call Initiated Survey Dismissed'; break;
            case 15: $row[] = 'Call Initiated Survey Ignored'; break;
            case 16: $row[] = 'Location Based Survey Finished'; break;
            case 17: $row[] = 'Location Based Survey Unfinished'; break;
            case 18: $row[] = 'Location Based Survey Dismissed'; break;
            case 19: $row[] = 'Location Based Survey Ignored'; break;
            default:
                $row[] = 'Undefined Status';
        }
        
        fputcsv($csv_file,$row,',','"');
    }
}

?>