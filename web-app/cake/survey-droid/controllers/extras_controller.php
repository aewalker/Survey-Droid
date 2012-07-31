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
class ExtrasController extends RestController
{
	//for php4
	var $name = 'Extras';
	var $components = array('Auth');

    /** Create */
    function rest_create() {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');

    }

    /** Read */
    function rest_read($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');

    }

    /** Update */
    function rest_update($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');

    }

    /** Delete */
    function rest_delete($id) {
        $this->autoRender = false;
        $this->header('HTTP/1.1 501 Not Implemented');

    }
}

?>