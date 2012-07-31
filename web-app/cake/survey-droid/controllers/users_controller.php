<?php 
/*---------------------------------------------------------------------------*
 * controllers/users_controller.php                                          *
 *                                                                           *
 * Controlls user-related functions: login, logout, registration, password   *
 * change, etc.                                                              *
 *---------------------------------------------------------------------------*/
/**
 * Controls access to the site via user login/logout.  Also provides site
 * admins to set up/edit/remove other users.
 * 
 * @author Sema Berkiten
 * @author Austin Walker
 */
App::import('Controller', 'Rest');
class UsersController extends RestController
{
	var $name = 'Users';
	
	//load the Auth (ie authorization) component
    var $components = array('Auth');
    
    var $helpers = array('Table');
    
    //TODO though it doesn't matter at this point,
    //it would be best to add callbacks like this to everything
    //to protect against future additions
    function beforeFilter()
	{
		parent::beforeFilter();
	}
	
	function beforeSave()
	{
		parent::beforeSave();
	}
	
	/**
	 * Show all users.
	 */
	function index(/*$showList = false*//* what??? */)
	{
		$this->set('users', $this->User->find('all', array(
			'fields' => array('id', 'username'),
			'order' => 'User.username ASC'
			))
		);
	}

	/**
	 * Atempt to log in a user.
	 */
    function login()
    {
    	//TODO there has got to be a more secure way to do this...
    	//chek to see if this is the first run.  If so, make the root user.
    	//TODO need to check that at least one user is an admin; if not, make
    	//the root user again
    	if (!$this->User->hasAny())
    	{
    		$this->User->create();
    		$this->User->save(array('User' => array(
	    		'username' => 'sdroot',
	    		'password' => $this->Auth->password('surveydroidpass'),
    			'password_copy' => 'surveydroidpass',
    			'password_confirm' => 'surveydroidpass',
    			'email' => 'noemail@nohost.com',
    			'admin' => 1,
    			'first_name' => 'Survey',
    			'last_name' => 'Droid'
    		)));
    	}
    	
    	//don't need to do anything else; Auth takes care of it in the view
    	//Auth will send the user to the post-login page (defaults to the home page)
    }
    
    /**
     * Logs the user out.
     */
    function logout()
    {
    	//sends user to the post-logout page (defaults to the login page)
    	$this->redirect($this->Auth->logout());
    }
    
    /**
     * Lets user change password, email, etc.
     */
    function manage()
    {
    	//lets user change password, email, etc.
    }
    
    /**
     * Register a new user.
     */
    function register()
    {
    	if( !empty( $this->data ) )
    	{
    		if (!empty($this->data['User']['password_copy']))
    		{
    			$this->data['User']['password'] = $this->data['User']['password_copy'];
    			$this->data['User']['password'] = $this->Auth->password($this->data['User']['password']);
    		}
			
    		//password is automatically cleaned and hashed by cake
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
    
    /**
     * User's profile page.
     */
	function profile()
    {
    	//main page when user logins
    }
    
    /**
     * Edit a user's information.
     * 
     * @param id - the user's id
     */
	function edit($id)
    {
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
    
    /**
     * Delte a user.
     * 
     * @param id - id of the user to delete
     */
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
