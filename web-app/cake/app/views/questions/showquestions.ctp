<?php
/*****************************************************************************
 * views/questions/index.ctp                                                 *
 *                                                                           *
 * Questions of a specific survey.                                           *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Questions');

echo $table->tableBody($results, array('Select' => array('url' => $ajax->link( 'Select', array ('controller' => 'branches', 'action' => 'showbranches', 'arg' => 'questionid' ), 
	array ('update' => 'branch' ) )) ) );
echo $table->endTable(array('Add Question' => array('command' => '/addquestion', 'arg' => 'questionid')));


?>