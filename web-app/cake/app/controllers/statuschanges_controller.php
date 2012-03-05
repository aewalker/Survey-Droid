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
class StatusChangesController extends RestController
{
	//for php4
	var $name = 'StatusChanges';
	var $components = array('Auth');

    function rest_create() {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');
    }
    function rest_read($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');
    }
    function rest_update($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');
    }
    function rest_delete($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');
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
            'recursive' => 0,
            'order' => 'StatusChange.created DESC'
        ));

        // custom stuff
        $csv_file = fopen('php://output', 'w');
        $headers = array('Time', 'Subject Id', 'Subject', 'Feature', 'Action');
        fputcsv($csv_file, $headers, ',', '"');

        foreach($models as $item) {
            $row = array();
            $row[] = $item['StatusChange']['created']; // Time

            $row[] = $item['StatusChange']['subject_id']; // Subject Id
            $row[] = $item['Subject']['first_name'] ." ". $item['Subject']['last_name']; // Subject


            switch ($item['StatusChange']['feature']) { // Feature
                case 0:
                    $row[] = 'GPS';
                    break;
                case 1:
                    $row[] = 'Call log';
                    break;
                case 2:
                    $row[] = 'Text log';
                    break;
                case 3:
                    $row[] = 'Surveys';
                    break;
                default:
                    $row[] = 'Undefined Feature';
            }

            switch ($item['StatusChange']['feature']) { // Action
                case 0:
                    $row[] = 'Disabling';
                    break;
                case 1:
                    $row[] = 'Enabling';
                    break;
                default:
                    $row[] = 'Undefined Status';
            }
            
            fputcsv($csv_file,$row,',','"');
        }
        fclose($csv_file);
    }
}

?>