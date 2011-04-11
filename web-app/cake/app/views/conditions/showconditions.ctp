<?php
/*****************************************************************************
 * views/conditions/showconditions.ctp                                           *
 *                                                                           *
 * Branches of a question.                                                   *
 *****************************************************************************/
 echo $this->Session->flash();

//show the results
echo '<br/><br/>';
echo $table->startTable('Condition');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/conditions/editcondition', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#con_space'),
            'Delete' => array(
                  'command' => '/conditions/deletecondition', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#con_space'),
            ));
		
echo $table->endTable(array('Add Condition' => array('command' => "/conditions/addcondition", 'arg' =>$branchid, 
					'type' => 'ajax',
					'update'=>'#con_space')));
                   
echo $this->Js->writeBuffer();

?>
