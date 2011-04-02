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
		//if (!empty($this->data['User']['password']))
		
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
    		if (!empty($this->data['User']['password_copy']))
    		{
    			$this->data['User']['password'] = $this->data['User']['password_copy'];
    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password']);
    		}
			
    		//sanitize the password
    		//$this->User->data = Sanitize::clean($this->data);
    		$this->User->create();
	    	if ($this->User->save($this->data))
	        {
	         	//new user saved successfully
	        	$this->redirect('/users/profile');
	    	}
	    	
	    	//clear the form
	    	$this->data['User']['password_copy'] = null;
	    	$this->data['User']['password_confirm'] = null;
    	}
    }
    
	function profile()
    {
    	//main page when user logins
    }
    
	function edituser()
    {
    	//edit user's information
    	if (($user = $this->Auth->user()) != NULL && !empty( $this->data ))
		{
			$user['password_confirm'] = $user['password'];   
    		if (!empty($this->data['User']['password_copy']))
    		{
    			$this->data['User']['password'] = $this->data['User']['password_copy'];
    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password']);
    			$user['password'] = $this->data['User']['password'];
    			$user['password_copy'] = $this->data['User']['password_copy'];
    			$user['password_confirm'] = $this->data['User']['password_confirm'];    			
    		}
			if (!empty($this->data['User']['username']))
    			$this->User->set('username', $this->data['User']['username']); 
    		if (!empty($this->data['User']['email']))
    			$user['email'] = $this->data['User']['email'];   
    		if (!empty($this->data['User']['first_name']))
    			$user['first_name'] = $this->data['User']['first_name'];   
    		if (!empty($this->data['User']['last_name']))
    			$user['last_name'] = $this->data['User']['last_name'];  
    		if ($this->data['User']['admin']==1)
    			$user['admin'] = 1;   
    		else
    			$user['admin'] = 0;   
   		echo $user['username']." ".$user['admin'];

	    	if ($this->User->save($user))
	        {
	         	//new user saved successfully
	        	//$this->redirect('/users/profile');
	        	echo "Successfully saved!";
	    	}
	    	
	    	//clear the form
	    	$this->data['User']['password_copy'] = null;
	    	$this->data['User']['password_confirm'] = null;
    	}
    }
}

?> 
