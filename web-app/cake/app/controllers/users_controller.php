<?php 
class UsersController extends AppController
{
    var $name = "Users";
     
    function beforeFilter()
	{
		parent::beforeFilter();
	}
    
    function login()
    {
        if(empty($this->data) == false)
        {
            if(($user = $this->User->validateLogin($this->data['User'])) == true)
            {
                $this->Session->write('User.userName', $user['username']);
				if ($user['admin'])
				{
					$this->Session->write('User.isAdmin', $user['admin']);
				}
                $this->Session->setFlash('You\'ve successfully logged in.');
                $this->redirect('index');
                exit();
            }
            else
            {
                $this->Session->setFlash('Sorry, incorrect or incomplete information.');
                exit();
            }
        }
    }
    
    function logout()
    {
        $this->Session->destroy('user');
        $this->Session->setFlash('You\'ve successfully logged out.');
        //$this->redirect('login');
    }
        
   
}

?> 
