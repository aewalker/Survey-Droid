<?php
/*****************************************************************************
 * views/data/locations.ctp                                            *
 *                                                                           *
 * Show subject list.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
//show the results
echo $table->startTable('Subject');
echo $table->tableBody($results, array(
            'Select' =>array(
                   'command' => '../datas/showlocations',
                   'arg' => 'id',
                   'type' => 'link')
            ));
echo $table->endTable(array(' ' =>' '));


?>
