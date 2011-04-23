<?php
/*****************************************************************************
 * views/data/showanswers.ctp                                            *
 *                                                                           *
 * Show answers.                                                       *
 *****************************************************************************/

echo $this->Session->flash();
$pagename = "Answers for: ".$questiontext;
$_SESSION['exportData'] = $results;
$_SESSION['exportColumnNames'] =  array(
					'Id', 'Choice Id', 'Answer Text', 'Time', 'Subject Id', 'Choice Text'
					);
$_SESSION['info'] = "";
$_SESSION['pagename'] = $pagename;

//show the results
echo $tablefordata->startTable('Answer', array(
					'Id', 'Choice Id', 'Answer Text', 'Time', 'Subject Id', 'Choice Text'
					));
echo $tablefordata->tableBody($results, array(),
            array(
            	'id', 'choice_id', 'ans_text', 'created', 'subject_id', 'choice_text'
            ));

echo $tablefordata->endTable(array('Export as xls file' => array('command' => "../datas/export_xls", 'arg' =>"", 
					'type' => 'link')));
echo $this->Js->writeBuffer();

?>

<div id='answers'>
</div>