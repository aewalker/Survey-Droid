<?php
/*****************************************************************************
 * views/data/subjectstatuses.ctp                                            *
 *                                                                           *
 * Show subjects' status.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

$_SESSION['exportData'] = $results;
$_SESSION['exportColumnNames'] =  array('Subject Id', 'Status', 'Feature', 'Time');
$_SESSION['info'] = "Status --> 1 for enabled, 0 for disable; Feature --> gps 0, call log, 1, text log 2, whole app 3";
$_SESSION['pagename'] = "Subjects' Status";

//show the results
echo $tablefordata->startTable('StatusChange', array('Subject Id', 'Status', 'Feature', 'Time'));

echo $tablefordata->tableBody($results, array(
            'Select' =>array(
                   'command' => '../subjects/view',
                   'arg' => 'subject_id',
                   'type' => 'link')
            ),
            array('subject_id', 'status', 'feature', 'created'));
            
echo $tablefordata->endTable(array('Export as xls file' => array('command' => "../datas/export_xls", 'arg' =>"", 
					'type' => 'link')));


?>
