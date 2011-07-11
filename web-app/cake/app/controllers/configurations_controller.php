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
 */
class ConfigurationsController extends AppController
{
	var $name = 'Configurations';
	
	/**
	 * Show all settings and let the user edit them.
	 */
	function index()
	{
		if ($this->data['Configuration']['confirm'] == true)
    	{
    		unset($this->data['Configuration']['confirm']); //very important!
    		print_r($this->data);
//			if ($this->Configuration->fromKeyVal($this->data))
//	        {
//	         	$this->Session->setFlash('Configuration settings saved!');
//	    	}
    	}
    	$this->set('data', $this->Configuration->toKeyVal());
	}
}
?>