<?php
/*---------------------------------------------------------------------------*
 * ssl.php                                                                   *
 *                                                                           *
 * Component that forces connections to use https.                           *
 *---------------------------------------------------------------------------*
 * This code adapted from from http://bakery.cakephp.org/articles/view/      *
 *   component-for-forcing-a-secure-connection                               *
 *---------------------------------------------------------------------------*/
/**
 * Used to force pages to load using https.
 */
class SslComponent extends Object {
	
	var $components = array('RequestHandler');
	
	var $Controller = null;
	
	function initialize(&$Controller) {
		$this->Controller = $Controller;
	}
	
	/**
	 * Calling this forces the page being loaded to use https.
	 */
	function force() {
		if(!$this->RequestHandler->isSSL()) {
			$this->Controller->redirect('https://'.$this->__url());
		}
	}
	
	/**
	 * Rewrites the current URL to use https.
	 */
	function __url() {
		$port = env('SERVER_PORT') == 80 ? '' : ':'.env('SERVER_PORT');

		return env('SERVER_NAME').$port.env('REQUEST_URI');
	}
}
?>