<?php
/*****************************************************************************
 * views/choices/showchoices.ctp                                             *
 *                                                                           *
 * Choices of a question.                                                    *
 *****************************************************************************/
echo $this->Session->flash();

echo '<br/><br/>';

//each time the choices are updated, update the branches
echo '<script>'.$this->Js->request("/branches/showbranches/$questionid",
	array('async' => true, 'update' => '#branches')).'</script>';

//show the results
echo '<br/><br/>';
echo $table->startTable('Choice');
echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => 'editchoice', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#ch_space'),
            'Delete' => array(
                  'command' => 'deletechoice', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#ch_space')
            ));
		
echo $table->endTable(array('Add Choice' => array('command' => "addchoice", 'arg' => $questionid, 
					'type' => 'ajax',
					'update'=>'#ch_space')));

echo $this->Js->writeBuffer();
?>
