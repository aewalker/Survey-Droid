<?php
/*---------------------------------------------------------------------------*
 * views/layouts/export_xls.ctp                                              *
 *                                                                           *
 * Layout used to export data as an .xls file.  Puts additional headers on   *
 * the response page to signify that it is a downloadable file.              *
 *---------------------------------------------------------------------------*/
header ("Last-Modified: " . gmdate("D,d M YH:i:s") . " GMT");
header ("Cache-Control: no-cache, must-revalidate");
header ("Pragma: no-cache");
header ("Content-type: application/vnd.ms-excel");
header ("Content-Disposition: attachment; filename=\"Report.xls" );
header ("Content-Description: Generated Report" );

echo $content_for_layout ?> 
