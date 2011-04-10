<?php
/*****************************************************************************
 * views/questions/index.ctp                                                 *
 *                                                                           *
 * Questions of a specific survey.                                           *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Question');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => 'editquestion', 'arg' => 'id', 'type' => 'link'),
            'Delete' => array(
                  'command' => 'deletequestion', 'arg' => 'id', 'type' => 'link'),
            'Select' =>array(
                   'command' => '../branches/showbranches',
                   'arg' => 'id',
                   'update'=>'branchdiv',
                   'type' => 'ajax')
            ));
		
echo $table->endTable(array('Add Question' => array('command' => "addquestion", 'type' => 'link')));

?>
<div class="branchdiv">
</div>