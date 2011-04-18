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


//show the results
echo $tablefordata->startTable('Location', array('Longitude', 'Latitude', 'Time'));

echo $tablefordata->tableBody($results, array(),
            array('longitude', 'latitude', 'created'));
$arg = "GPS information for subject ".$subjectid;
echo $tablefordata->endTable(array('Export as xls file' => array('command' => "../datas/export_xls", 'arg' =>$arg, 
					'type' => 'link')));
?>