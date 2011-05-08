<?php
/*---------------------------------------------------------------------------*
 * views/datas/locations.ctp                                                 *
 *                                                                           *
 * Show subject list with links to location data for each one.               *
 *---------------------------------------------------------------------------*/
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
