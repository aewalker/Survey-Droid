<?php
/*****************************************************************************
 * views/questions/index.ctp                                                 *
 *                                                                           *
 * Questions of a specific survey.                                           *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Question',  array('class' => array('table' => 'questions')));
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/questions/editquestion',
                  'arg' => 'id',
                  'type' => 'ajax',
                  'update' => '#question_space_'),
            'Delete' => array(
                  'command' => '/questions/deletequestion',
                  'arg' => 'id',
                  'type' => 'ajax',
                  'update' => '#question_space_'),
            'Choices' =>array(
                   'command' => '/choices/showchoices',
                   'arg' => 'id',
                   'update'=>'#question_choices_',
                   'type' => 'ajax'),
            'Branches' =>array(
                   'command' => '/branches/showbranches',
                   'arg' => 'id',
                   'update'=>'#question_branches_',
                   'type' => 'ajax')
            ), array(), array('choices', 'choices_space', 'branches', 'branches_space'));
		
echo $table->endTable(array('Add Question' => array
(
	'command' => '/questions/addquestion',
	'arg' => $surveyid,
	'type' => 'ajax',
	'update' => '#questions_space'
)));
echo $this->Js->writeBuffer();

?>

