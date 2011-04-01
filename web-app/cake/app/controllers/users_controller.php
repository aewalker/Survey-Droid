<?php 
/*****************************************************************************
 * controllers/users_controller.php                                          *
 *                                                                           *
 * Controlls user-related functions: login, logout, registration, password   *
 * change, etc.                                                              *
 *****************************************************************************/
class UsersController extends AppController
{
	//load the Auth (ie authorization) component
    var $components = array('Auth');
     
    function beforeFilter()
	{
		parent::beforeFilter();
	}
	
	function beforeSave()
	{
		//Need to tell Auth to hash the confirm password so the model can check that they match
		if (!empty($tis->data['User']['confirm_pass']))
		$this->data['User']['confirm_pass'] = $this->Auth->password($this->data['User']['confirm_pass']);
		parent::beforeSave();
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
    
    function manage()
    {
    	//lets user change password, email, etc.
    }
    
    function register()
    {
    	//register a new user
    	if( !empty( $this->data ) )
    	{
    		// get hash coded password
    		$this->data['User']['passwrd'] = $this->Auth->password($this->data['User']['passwrd']);
    		//sanitize the password
    		//$this->User->data = Sanitize::clean($this->data);
	    	if ($this->User->save())
	        {
	         	//new user saved successfully
	        	$this->redirect('/users/profile');
	    	}
	    	
	    	//clear the form
	    	$this->data['User']['passwrd'] = null;
    	}
    }
}

?> 
