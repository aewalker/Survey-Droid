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
                  'command' => 'editcondition', 'arg' => 'id', 'type' => 'link'),
            'Delete' => array(
                  'command' => 'deletecondition', 'arg' => 'id', 'type' => 'link')
            ));
		
echo $table->endTable(array('Add Condition' => array('command' => "addcondition", 'update'=>'#conditiondiv', 'type' => 'ajax')));

?>