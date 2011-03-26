<?php 
class UsersController extends AppController
{
	//load the Auth (ie authorization) component
    public $components = array('Auth');
     
    function beforeFilter()
	{
		parent::beforeFilter();
	}
	
	function index()
	{
		//we want the default action of the users controller to be login (for now)
		$this->redirect('login');
	}
    
    function login()
    {
    	//don't need to do anything; Auth takes care of it in the view
    	//Auth will send the user to the post-login page (defaults to the home page)
    }
    
    function logout()
    {
    	//sends user to the post-logout page (defaults to the login page)
    	$this->redirect($this->Auth->logout());
    }
}

?> 
