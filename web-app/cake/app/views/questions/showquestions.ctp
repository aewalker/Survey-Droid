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
                  'command' => '/questions/editquestion', 'arg' => 'id', 'type' => 'ajax', 'update' => '#q_space'),
            'Delete' => array(
                  'command' => '/questions/deletequestion', 'arg' => 'id', 'type' => 'ajax', 'update' => '#q_space'),
            'Select' =>array(
                   'command' => '/branches/showbranches',
                   'arg' => 'id',
                   'update'=>'#branches',
                   'type' => 'ajax')
            ));
		
echo $table->endTable(array('Add Question' => array('command' => '/questions/addquestion', 'arg' => $surveyid, 'type' => 'ajax', 'update' => '#q_space')));
echo $this->Js->writeBuffer();

?>

<div id="questions">
</div>

