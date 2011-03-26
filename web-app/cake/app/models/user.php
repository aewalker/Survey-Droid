<?php 
class User extends AppModel
{   
	/* Don't need this since Auth is taking care of it 
    function validateLogin($data)
    {
        $user = $this->find(array('username' => $data['username'], 'password' => sha1($data['password'])), array('id', 'username'));
        if(empty($user) == false)
            return $user['User'];
        return false;
    }
    */
}
?> 
