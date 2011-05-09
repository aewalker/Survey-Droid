<?php
/*---------------------------------------------------------------------------*
 * views/subject/add.ctp                                                     *
 *                                                                           *
 * Page to add a subject.                                                    *
 *---------------------------------------------------------------------------*/

//main form
echo $form->create('Subject', array('action' => 'add'));
echo $form->input('first_name');
echo $form->input('last_name');
echo $form->input('phone_num');
echo $form->input('device_id', array('type' => 'text'));
echo $form->end('Create');

//cancel button
echo $form->create('Subject', array('action' => 'index'));
echo $form->end('Cancel');
?>