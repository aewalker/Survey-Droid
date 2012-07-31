<?php
/*---------------------------------------------------------------------------*
 * views/datas/answers.ctp                                                   *
 *                                                                           *
 * Show questions with links to answer data for each one.                    *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

//show the results
echo $table->startTable('Question');
echo $table->tableBody($results, array(
            'Select' =>array(
                   'command' => '../datas/showanswers',
                   'arg' => 'id',
                  // 'update'=>'#answersdiv',
                   'type' => 'link')
            ));
		
echo $table->endTable(array(' ' =>' '));
echo $this->Js->writeBuffer();

?>
<div id="answersdiv">
</div>