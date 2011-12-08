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
	var $helpers = array('Cache');
	
	var $cacheAction = array(
	   'rest/answers' => 7200
	);

    function rest_index() {
        $this->autoRender = false;
        $this->cacheAction = '2 hours';
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
            'conditions' => $conditions
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
}

?>