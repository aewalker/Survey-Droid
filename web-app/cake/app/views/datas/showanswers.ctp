<?php
/*---------------------------------------------------------------------------*
 * views/data/showanswers.ctp                                                *
 *                                                                           *
 * Show answers.                                                             *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

$pagename = "Answers for: ".$questiontext;
$_SESSION['exportData'] = $results;
$_SESSION['exportColumnNames'] =  array(
	'Id', 'Choice Id', 'Answer Text', 'Time', 'Subject Id', 'Choice Text'
	);
$_SESSION['info'] = "";
$_SESSION['pagename'] = $pagename;

echo "<h2>$pagename</h2>";
//show the results
if ($type == 'multipleChoice')
{ //adapt the results table based on the kind of quetion:
  //don't show text or value for multiple choice...
	echo $tablefordata->startTable('Answer', array(
						'Choice Ids', 'Time', 'Subject Id', 'Choice Text'
						));
	echo $tablefordata->tableBody($results, array(),
	            array(
	            	'choice_ids', 'created', 'subject_id', 'choice_text'
            ));
}
else if ($type == 'freeResponse')
{ //...don't show choices or value for free response...
	echo $tablefordata->startTable('Answer', array(
						'Answer Text', 'Time', 'Subject Id'
						));
	echo $tablefordata->tableBody($results, array(),
	            array(
	            	'ans_text', 'created', 'subject_id'
            ));
}
else if ($type == 'scale')
{ //...don't show choices or text for scale...
	echo $tablefordata->startTable('Answer', array(
						'Answer Text', 'Time', 'Subject Id'
						));
	echo $tablefordata->tableBody($results, array(),
	            array(
	            	'ans_text', 'created', 'subject_id'
            ));
}

echo $tablefordata->endTable(array('Export as xls file' => array(
	'command' => "../datas/export_xls",
	'arg' =>"", 
	'type' => 'link')));
echo $this->Js->writeBuffer();
?>

<div id='answers'>
</div>