<?php
/*****************************************************************************
 * views/data/calls.ctp                                            *
 *                                                                           *
 * Show subject list.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
//show the results
echo $table->startTable('Subject');
echo $table->tableBody($results, array(
            'Select' =>array(
                   'command' => '../datas/showcalls',
                   'arg' => 'id',
                   'type' => 'link')
            ));
echo $table->endTable(array(' ' =>' '));

?>
