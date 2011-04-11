<?php
/*****************************************************************************
 * views/choices/showchoices.ctp                                           *
 *                                                                           *
 * Choices of a question.                                                   *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo '<br/><br/>';
echo $table->startTable('Choice');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/choices/editchoice', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#ch_space'),
            'Delete' => array(
                  'command' => '/choices/deletechoice', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#ch_space')
            ));
		
echo $table->endTable(array('Add Choice' => array('command' => "/choices/addchoice", 'arg' =>$questionid, 
					'type' => 'ajax',
					'update'=>'#ch_space')));
                   
echo $this->Js->writeBuffer();

?>
