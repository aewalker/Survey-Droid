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
	
	function index($showList = false)
	{
		//we want the default action of the users controller to be login (for now)
		//$this->redirect('login');
		$results = $this->User->find('all', array('order' => 'User.username ASC', 'limit' => 20));
		foreach($results as &$result)
			$result = $result['User'];
		$this->set('users', $results);
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
    
	function edituser($id)
    {
    	//edit user's information
    	//if (!$this->Session->check('User.admin'))
		//{		
			$results = $this->User->find('all', array('conditions' => array('User.id' => $id)));
			foreach($results as &$result)
			{
				$result = $result['User'];
				$this->set('user', $result);
			}
			
			if (!empty($this->data['User']['password_copy']))
    		{
    			$this->data['User']['password'] = $this->data['User']['password_copy'];
    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password']);
    		}
		
			$result = $this->User->save($this->data);
			
			if ($result)
			{
				$this->set('result', true);
			}
			else
			{
				echo 'not saved';
				$this->set('result', $this->User->validationErrors);
				$this->set('id', $id);
			}
			
			//$this->User->read(null, $this->data['User']['id']);
			
			/*$user = $this->User->read(array('id', 'password', 'email', 'username', 'first_name', 'last_name', 'admin'), $this->data['User']['id']);
			echo $user['username']." ".$user['email']."<br/>";
    		if (!empty($this->data['User']['password_copy']) && 
    				($this->data['User']['password_copy']==$this->data['User']['password_confirm']) )
    			$this->data['password'] = $this->Auth->password($this->data['User']['password_copy']); 
    			
			if (empty($this->data['User']['username']))
    			$this->data['username'] = $user['username']; 
    		if (empty($this->data['User']['email']))
    			$this->data['email'] = $user['email'];   
    		if (!empty($this->data['User']['first_name']))
    			$this->data['first_name'] = $user['first_name']; 
    		if (!empty($this->data['User']['last_name']))
    			$this->data['last_name'] = $user['last_name'];  			
    		
    		$this->User->save($this->data);
	    	echo $this->data['User']['username']." ".$this->data['User']['email'];
	    	//clear the form
	    	$this->data['User']['password_copy'] = null;
	    	$this->data['User']['password_confirm'] = null;
	    	*/
    	//}
    }
}

?> 
