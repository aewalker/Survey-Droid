<?php 
class User extends AppModel
{
    var $name = 'User';
    
    function validateLogin($data)
    {
        $user = $this->find(array('email' => $data['username'], 'pass' => sha1($data['password'])), array('id', 'username'));
        if(empty($user) == false)
            return $user['User'];
        return false;
    }
    
}
?> 
