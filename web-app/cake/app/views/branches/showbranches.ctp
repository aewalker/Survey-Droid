<?php
/*****************************************************************************
 * views/branches/showbranches.ctp                                           *
 *                                                                           *
 * Branches of a question.                                                   *
 *****************************************************************************/
echo $this->Session->flash();
echo $this->Js->link(' ', "/choices/showchoices/$questionid", array('async' => true, 'update' => '#choices'));

//show the results
echo '<br/><br/>';
echo $table->startTable('Branch');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/branches/editbranch', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#b_space'),
            'Delete' => array(
                  'command' => '/branches/deletebranch', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#b_space'),
            'Select' =>array(
                   'command' => '/conditions/showconditions',
                   'arg' => 'id',
                   'type' => 'ajax',
                   'update'=>'#conditions')

            ));
		
echo $table->endTable(array('Add Branch' => array('command' => "/branches/addbranch", 'arg' =>$questionid, 
					'type' => 'ajax',
					'update'=>'#b_space')));
                   
echo $this->Js->writeBuffer();

?>
