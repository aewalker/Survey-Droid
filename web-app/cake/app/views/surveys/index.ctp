<?php
/*---------------------------------------------------------------------------*
 * views/survey/index.ctp                                                    *
 *                                                                           *
 * Survey overview page.                                                     *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

//show the results
echo $table->startTable('Survey');
echo $table->tableBody($results, array(
	'Edit' => array(
		'command' => 'editsurvey', 'arg' => 'id', 'type' => 'link'),
	'Delete' => array(
		'command' => 'deletesurvey', 'arg' => 'id', 'type' => 'link'),
	'Edit Survey Questions' => array(
		'command' => 'viewsurvey', 'arg' => 'id', 'type' => 'link' ) 
	), array('id', 'name'));
echo $table->endTable(array('Add Survey' =>
	array('command' => 'addsurvey', 'type' => 'link', 'arg' => NULL)));
?>