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
    	if (!empty( $this->data ))
		{			
    		if (!empty($this->data['User']['password_copy']) && 
    				($this->data['User']['password_copy']==$this->data['User']['password_confirm']) )
    			$this->User->set('password', $this->Auth->password($this->data['User']['password'])); 
    			   			
			if (!empty($this->data['User']['username']))
    			$this->User->set('username', $this->data['User']['username']); 
    		if (!empty($this->data['User']['email']))
    			$this->User->set('email', $this->data['User']['email']);   
    		if (!empty($this->data['User']['first_name']))
    			$this->User->set('first_name', $this->data['User']['first_name']);   
    		if (!empty($this->data['User']['last_name']))
    			$this->User->set('last_name', $this->data['User']['last_name']);  
    		if ($this->data['User']['admin']==1)
    			$this->User->set('admin', 1);   
    		else
    			$this->User->set('admin', 0);   

	    	if ($this->User->save($user))
	        {
	         	//new user saved successfully
	        	//$this->redirect('/users/profile');
	        	echo "Successfully saved!";
	    	}
	    	else
	    		echo "Error!";
	    	
	    	
	    	//clear the form
	    	$this->data['User']['password_copy'] = null;
	    	$this->data['User']['password_confirm'] = null;
    	}
    }
}

?> 
