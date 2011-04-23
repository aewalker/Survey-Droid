<?php
/*****************************************************************************
 * views/data/showlocation.ctp                                            *
 *                                                                           *
 * Show GPS information.                                                       *
 *****************************************************************************/
echo $this->Session->flash();

$_SESSION['exportData'] = $results;
$_SESSION['exportColumnNames'] =  array('Longitude', 'Latitude', 'Time');
$_SESSION['info'] = "";
$_SESSION['pagename'] = "GPS information for subject ".$subjectid;

//show the results
echo $tablefordata->startTable('Location', array('Longitude', 'Latitude', 'Time'));

echo $tablefordata->tableBody($results, array(),
            array('longitude', 'latitude', 'created'));

echo $tablefordata->endTable(array('Export as xls file' => array('command' => "../datas/export_xls", 'arg' =>"", 
					'type' => 'link')));
?>