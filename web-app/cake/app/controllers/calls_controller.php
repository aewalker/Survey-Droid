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

class CallsController extends AppController
{
	//for php4
	var $name = 'Calls';
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
            'recursive' => 0,
            'conditions' => $conditions,
            'limit' => 300,
            'order' => 'Call.created DESC'
        ));

        // custom stuff
        $arr = array();
        foreach($models as $item) {
            $item['Call']['subject'] = $item['Subject'];
            $arr[] = $item['Call'];
        }
        e(json_encode($arr));
    }
}

?>