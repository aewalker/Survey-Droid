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

class AnswersController extends AppController
{
	//for php4
	var $name = 'Answers';
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
            'recursive' => 1,
            'conditions' => $conditions,
            'limit' => 300,
            'order' => 'Answer.created DESC'
        ));

        // custom stuff
        $arr = array();
        foreach($models as $item) {
            $item['Answer']['choices'] = $item['Choice'];
            $item['Answer']['question'] = $item['Question'];
            $item['Answer']['survey_id'] = $item['Question']['survey_id'];
            $item['Answer']['subject'] = $item['Subject'];
            $arr[] = $item['Answer'];
        }
        e(json_encode($arr));
    }

    /** csv dump */
    function dump() {
        $this->autoRender = false;
        ini_set('max_execution_time', 600); //increase max_execution_time to 10 min if data set is very large

        $modelClass = $this->modelClass;
        $filename = $modelClass . "_dump_".date("Y.m.d").".csv";
        
        header('Content-type: application/csv');
        header('Content-Disposition: attachment; filename="'.$filename.'"');


        $models = $this->$modelClass->find('all', array(
            'recursive' => 1,
            'order' => 'Answer.created DESC'
        ));

        // custom stuff
        $csv_file = fopen('php://output', 'w');
        $headers = array('Subject ID', 'Subject', 'Survey ID', 'Question', 'Answer', 'Answer Type', 'Time Answered');
        fputcsv($csv_file, $headers, ',', '"');

        foreach($models as $item) {
            $row = array();
            $row[] = $item['Answer']['subject_id']; // Subject Id
            $row[] = $item['Subject']['first_name'] ." ". $item['Subject']['last_name']; // Subject
            $row[] = $item['Question']['survey_id']; // Survey Id
            $row[] = $item['Question']['q_text']; // Question

            switch ($item['Answer']['ans_type']) { // Answer
                case 0:
                    $choices = array();
                    foreach ($item['Choice'] as $choice) {
                        $choices[] = $choice['choice_text'];
                    }
                    $row[] = implode(', ', $choices);
                    break;
                case 1:
                    $row[] = $item['Answer']['ans_value'];
                    break;
                case 2:
                    $row[] = $item['Answer']['ans_text'];
                    break;
                default:
                    $row[] = 'Undefined Type';
            }

            switch ($item['Answer']['ans_type']) { // Answer type
                case 0:
                    $row[] = 'Single/Multiple Choice';
                    break;
                case 1:
                    $row[] = 'Numerical Value';
                    break;
                case 2:
                    $row[] = 'Text';
                    break;
                default:
                    $row[] = 'Undefined Type';
            }

            $row[] = $item['Answer']['created']; // Time Answered
            
            fputcsv($csv_file,$row,',','"');
        }
        fclose($csv_file);
    }
}

?>