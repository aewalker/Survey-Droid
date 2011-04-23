<?php
/*****************************************************************************
 * views/data/showcalls.ctp                                            *
 *                                                                           *
 * Show call logs.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

$_SESSION['exportData'] = $results;
$_SESSION['exportColumnNames'] =  array('Contact Id', 'Call Type', 'Duration', 'Time');
$_SESSION['info'] = "Call type --> outgoing call 0, incoming call 1, outgoing text 2, incoming text 3, missed call 4";
$_SESSION['pagename'] = "Call logs for subject ".$subjectid;
//show the results
echo $tablefordata->startTable('Call', array('Contact Id', 'Call Type', 'Duration', 'Time'));

echo $tablefordata->tableBody($results, array(),
            array('contact_id', 'type', 'duration', 'created'));
            
echo $tablefordata->endTable(array('Export as xls file' => array('command' => "../datas/export_xls", 'arg' =>"", 
					'type' => 'link')));
?>