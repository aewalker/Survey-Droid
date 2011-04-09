<?php
/*****************************************************************************
 * views/survey/index.ctp                                                   *
 *                                                                           *
 * Survey overview page.                                                   *
 *****************************************************************************/
echo $this->Session->flash();

//show the results
echo $table->startTable('Survey');
echo $table->tableBody($results, array('Select' => array('url' => '/questions/showquestions', 'arg' => 'surveyid') ) );
echo $table->endTable(array('Add Survey' => array('command' => 'addsurvey')));
?>