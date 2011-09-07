<?php 
/*---------------------------------------------------------------------------*
 * controllers/rest_controller.php                                           *
 *                                                                           *
 * Not meant to be involed directly, should be extended to add REST          *
 * functionality                                                             *
 *                                                                           *
 * @author Tony Xiao                                                         *
 *---------------------------------------------------------------------------*/
/**
 * TODO: Refactor so I don't have to keep on repeating $this->autoRender = false
 * TODO: and $this->data = json_decode(file_get_contents('php//input'), true)
 */
class RestController extends AppController
{
    /** Get a list of whatever model we are working with, with arbitrary filtering */
    function rest_index() {
        $this->autoRender = false;
        $modelClass = $this->modelClass;

        // add any applicable filters
        $conditions = array();
        if ($this->params['url'])
            foreach ($this->params['url'] as $field => $value)
                if ($field != 'url' && array_key_exists($field, $this->$modelClass->_schema))
                    $conditions[$modelClass.'.'.$field] = $value;

        $models= $this->$modelClass->find('all', array(
            'recursive' => -1,
            'conditions' => $conditions
        ));
        e(json_encode($models));
    }

    /** Create */
    function rest_create() {
        $this->autoRender = false;
        $modelClass = $this->modelClass;
        $this->data = json_decode(file_get_contents('php://input'), true);
        if (!empty($this->data)) {
            unset($this->data[$modelClass]['id']); // disallow client-assigned id
            if ($this->$modelClass->save($this->data)) {;
                $this->header('HTTP/1.1 201 Created');
                e(json_encode($this->$modelClass->read())); // TODO: read() returns associated models, which is unintended
                return;
            }
        }
        $this->header('HTTP/1.1 400 Bad Request');
    }

    /** Read */
    function rest_read($id) {
        $this->autoRender = false;
        $modelClass = $this->modelClass;
        $model = $this->$modelClass->find('first', array(
            'conditions' => array($modelClass.'.id' => $id),
            'recursive' => -1
        ));
        if ($model) {
            e(json_encode($model));
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }

    /** Update */
    function rest_update($id) {
        $this->autoRender = false;
        $modelClass = $this->modelClass;
        if ($this->$modelClass->read(null, $id)) {
            $this->data = json_decode(file_get_contents('php://input'), true);
            if (!empty($this->data)) {
                unset($this->data[$modelClass ]['id']); // disallow client-assigned id
                if ($this->$modelClass->save($this->data)) {;
                    e(json_encode($this->$modelClass->read()));
                    return;
                }
            }
            $this->header('HTTP/1.1 400 Bad Request');
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }

    /** Delete */
    function rest_delete($id) {
        $this->autoRender = false;
        $modelClass = $this->modelClass;
        if($this->$modelClass->delete($id)) {
            $this->header('HTTP/1.1 204 No Content');
            return;
        }
        $this->header('HTTP/1.1 404 Not Found');
    }
}
?>