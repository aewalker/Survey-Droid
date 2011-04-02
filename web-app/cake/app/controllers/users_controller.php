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

			if(isset($this->data['User']['id']))
			{
				if (!empty($this->data['User']['password_copy']))
	    		{
	    			$this->data['User']['password'] = $this->data['User']['password_copy'];
	    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password']);
	    		}
	    		else
	    		{
	    			$this->data['User']['password'] = $result['password'];
	    			$this->data['User']['password_confirm'] = $result['password'];
	    			$this->data['User']['password_copy'] = $result['password'];
	    		}
			
				$saved = $this->User->save($this->data);
				
				if ($saved)
				{
					$this->set('saved', true);
				}
				else
				{
					$this->set('saved', $this->User->validationErrors);
					$this->set('id', $id);
				}
			}
			
    	//}
    }
    
	function deleteuser($id)
    {
    	//if ($this->Session->check('User.isAdmin'))
		//{
    			if ($this->data['User']['confirm'] == 'true')
				{
					if ($id != null)
					{
						$this->set('result', $this->User->delete($id));
					}
					else
					{
						$this->set('result', false);
					}
				}
				else
				{
					$this->set('id', $id);
				}
		//}
    	
    }

}

?> 
