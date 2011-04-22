<?php
/*****************************************************************************
 * views/choices/showchoices.ctp                                             *
 *                                                                           *
 * Choices of a question.                                                    *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable("Choice", array('class' => array('table' => 'choices')));
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => 'editchoice',
                  'arg' => 'id',
                  'type' => 'ajax',
                  'update'=> '#choice_space_'),
            'Delete' => array(
                  'command' => 'deletechoice',
                  'arg' => 'id',
                  'type' => 'ajax',
                  'update'=> '#choice_space_')
            ));
		
echo $table->endTable(array('Add Choice' => array
(
	'command' => 'addchoice',
	'arg' => $questionid,
	'type' => 'ajax',
	'update'=> "#question_choices_space_$questionid"
)));

echo $this->Js->writeBuffer();
?>
