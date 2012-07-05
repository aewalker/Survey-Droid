<?php 
/*---------------------------------------------------------------------------*
 * controllers/configurations_controller.php                                 *
 *                                                                           *
 * Allows the user to edit the configuration settings.                       *
 *---------------------------------------------------------------------------*/
/**
 * Shows and allows users to change survey configurations settings.
 * 
 * @author Austin Walker
 * @author Tony Xiao
 */
App::import('Controller', 'Rest');
class ConfigurationsController extends RestController
{
	var $name = 'Configurations';
	
	/**
	 * Show all settings and let the user edit them.
	 */
	function index()
	{
		if ($this->data['confirm'] == true)
    	{
    		unset($this->data['confirm']); //very important!
			if ($this->Configuration->fromKeyVal($this->data))
	        {
	         	$this->Session->setFlash('Configuration settings saved!');
	    	}
    	}
    	$this->set('data', $this->Configuration->toKeyVal());
	}
}
?>