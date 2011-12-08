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

    var $helpers = array('Cache');
	
	var $cacheAction = array(
	   'rest/locations' => 7200
	);


    function rest_index() {
        $this->autoRender = false;
//        $this->header('Content-Type: application/json');
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
            'limit' => $limit,
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
}

?>