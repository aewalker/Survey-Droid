<?php 
/*****************************************************************************
 * controllers/users_controller.php                                          *
 *                                                                           *
 * Controlls user-related functions: login, logout, registration, password   *
 * change, etc.                                                              *
 *****************************************************************************/
class UsersController extends AppController
{
	var $name = 'Users';
	//load the Auth (ie authorization) component
    var $components = array('Auth');
    
    var $helpers = array('Table');
     
    function beforeFilter()
	{
		parent::beforeFilter();
	}
	
	function beforeSave()
	{
		parent::beforeSave();
	}
	
	function index($showList = false)
	{
		$this->set('users', $this->User->find('all', array(
			'fields' => array('id', 'username'),
			'order' => 'User.username ASC'
			))
		);
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
    
	function edit($id)
    {
    	//edit user's information

		$results = $this->User->find('all', array('conditions' => array('User.id' => $id)));
		foreach($results as &$result)
		{
			$result = $result['User'];
			$this->set('user', $result);
		}
		
		$this->set('saved', false);

		if(isset($this->data['User']['id']))
		{
			if (empty($this->data['User']['password_copy']) && empty($this->data['User']['password_confirm']))
    		{
    			$this->data['User']['password_confirm'] = "XXXXXXXX";
    			$this->data['User']['password_copy'] = $this->data['User']['password_confirm'];
    			$this->data['User']['password'] = $result['password'];
    		}
    		else
    		{
    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password_copy']);
    		}

			$saved = $this->User->save($this->data);
			
			if ($saved)
			{
				$this->Session->setFlash('User is edited!');
				$this->redirect('/users');
			}
			else
			{
				$this->set('saved', $this->User->validationErrors);
				$this->set('id', $id);
			}
			
		}
			
    }
    
	function delete($id)
    {
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
    	
    }

}

?> 
