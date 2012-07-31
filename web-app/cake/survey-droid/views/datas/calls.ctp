<?php
/*---------------------------------------------------------------------------*
 * views/datas/calls.ctp                                                     *
 *                                                                           *
 * Show subject list with links to call data for each one.                   *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

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
