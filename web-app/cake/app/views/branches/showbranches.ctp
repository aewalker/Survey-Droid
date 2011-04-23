<?php
/*****************************************************************************
 * views/branches/showbranches.ctp                                           *
 *                                                                           *
 * Branches of a question.                                                   *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Branch', array('class' => array('table' => 'branches')));
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/branches/editbranch', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#branch_space_'),
            'Delete' => array(
                  'command' => '/branches/deletebranch', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#branch_space_'),
            'Conditions' =>array(
                   'command' => '/conditions/showconditions',
                   'arg' => 'id',
                   'type' => 'ajax',
                   'update'=>'#branch_conditions_')
            ), array(), array('conditions', 'conditions_space'));
		
echo $table->endTable(array('Add Branch' => array('command' => "/branches/addbranch", 'arg' => $questionid, 
					'type' => 'ajax',
					'update' => "#question_branches_space_$questionid")));

echo $this->Js->writeBuffer();

?>
