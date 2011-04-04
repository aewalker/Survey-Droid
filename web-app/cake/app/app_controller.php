<?php
/*****************************************************************************
 * app_controller.php                                                        *
 *                                                                           *
 * Holds application-wide controller-related code.                           *
 *****************************************************************************/
class AppController extends Controller
{
	//load the Auth (ie authorization) and Session components for all controllers
    var $components = array('Auth', 'Session');
    
    //load helpers
    var $helpesr = array('Html', 'Form');
}
?>