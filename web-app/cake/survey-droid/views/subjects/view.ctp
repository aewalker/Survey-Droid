<?php
/*---------------------------------------------------------------------------*
 * views/subject/view.ctp                                                    *
 *                                                                           *
 * Page to view a subject's info.                                            *
 *---------------------------------------------------------------------------*/
 echo $html->css('peoples');
 ?> 
 <table>
 <?php

 echo $html->tableCells(
      array(array(
          'First Name',
          $first_name
     )));

echo $html->tableCells(
      array(array(
          'Last Name',
          $last_name
     )));

echo $html->tableCells(
      array(array(
          'Phone Number',
          $phone_num
     )));
     
echo $html->tableCells(
      array(array(
          'DeviceID',
          $device_id
     )));

?>
</table>