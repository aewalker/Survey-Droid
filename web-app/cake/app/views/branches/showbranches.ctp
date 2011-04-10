<?php
/*****************************************************************************
 * views/branches/showbranches.ctp                                           *
 *                                                                           *
 * Branches of a question.                                                   *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo '<br/><br/>';
echo $table->startTable('Branch');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => 'editbranch', 'arg' => 'id', 'type' => 'link'),
            'Delete' => array(
                  'command' => 'deletebranch', 'arg' => 'id', 'type' => 'link'),
            'Select' =>array(
                   'command' => '../../conditions/showconditions',
                   'arg' => 'id',
                   'update'=>'#conditiondiv',
                   'type' => 'ajax')
            ));
		
echo $table->endTable(array('Add Branch' => array('command' => "../addbranch/$questionid", 
					'update'=>'#addbranchdiv',
                   'type' => 'ajax')));

?>
<div id="conditiondiv">
</div>
<div id="addbranchdiv">
</div>