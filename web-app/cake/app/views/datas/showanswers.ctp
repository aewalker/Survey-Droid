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
if ($multipleChoice)
{ //adapt the results table based on the kind of quetion:
  //don't show text for multiple choice...
	echo $tablefordata->startTable('Answer', array(
						'Choice Id', 'Time', 'Subject Id', 'Choice Text'
						));
	echo $tablefordata->tableBody($results, array(),
	            array(
	            	'choice_id', 'created', 'subject_id', 'choice_text'
            ));
}
else
{ //...and don't show choices for free response
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