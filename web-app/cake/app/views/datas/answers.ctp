<?php
/*****************************************************************************
 * views/data/answers.ctp                                            *
 *                                                                           *
 * Show answers.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Question');
echo $table->tableBody($results, array(
            'Select' =>array(
                   'command' => '/datas/showanswers',
                   'arg' => 'id',
                   'update'=>'#answers',
                   'type' => 'ajax')
            ));
		
echo $table->endTable(array(' ' =>' '));
echo $this->Js->writeBuffer();

?>

<div id='answers'>
</div>