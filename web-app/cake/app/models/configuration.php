<?php
/*---------------------------------------------------------------------------*
 * models/configuration.php                                                  *
 *                                                                           *
 * Model for phone configuration settings.                                   *
 *---------------------------------------------------------------------------*/
/**
 * Model for the configuration settings used by the phones.
 * 
 * @author Austin Walker
 */
class Configuration extends AppModel
{
	//for php4
	var $name = 'Configuration';
	
	//TODO validate the opt field (must be an operator (eg ==, >=, etc))
}
?>