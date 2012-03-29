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
class LocationsController extends RestController
{
	//for php4
	var $name = 'Locations';
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
        $limit = array_key_exists('limit', $this->params['url']) ? $this->params['url']['limit'] : 100;
        $models = $this->$modelClass->find('all', array(
            'recursive' => 0,
            'conditions' => $conditions,
            'limit' => 300,
            'order' => 'Location.created DESC'
        ));

        // custom stuff
        $arr = array();
        foreach($models as $item) {
            $item['Location']['subject'] = $item['Subject'];
            $arr[] = $item['Location'];
        }
        e(json_encode($arr));
    }

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

    function dump() {
        $this->autoRender = false;
        ini_set('max_execution_time', 600); //increase max_execution_time to 10 min if data set is very large
        
        $modelClass = $this->modelClass;
        $filename = $modelClass . "_dump_".date("Y.m.d").".csv";

        header('Content-type: application/csv');
        header('Content-Disposition: attachment; filename="'.$filename.'"');

        // custom stuff
        $csv_file = fopen('php://output', 'w');
        $headers = array_keys($this->$modelClass->_schema);
        fputcsv($csv_file, $headers, ',', '"');

        $total = $this->$modelClass->find('count');
        $increment = 100;

        for ($offset = 0; $offset<$total; $offset+=$increment) {
            $models = $this->$modelClass->find('all', array(
                'recursive' => -1,
                'order' => 'created DESC',
                'offset' => $offset,
                'limit' => $increment
            ));
            foreach($models as $item) {
                $row = array();
                foreach ($headers as $header) {
                    array_push($row, $item[$modelClass][$header]);
                }
                fputcsv($csv_file,$row,',','"');
            }
        }
    
        fclose($csv_file);
    }
}

?>