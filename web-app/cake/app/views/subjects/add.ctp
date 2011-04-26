<?php
/*****************************************************************************
 * views/subject/add.ctp                                                     *
 *                                                                           *
 * Page to add a subject.                                                    *
 *****************************************************************************/
echo $form->create('Subject', array('action' => 'add'));
echo $form->input('first_name');
echo $form->input('last_name');
echo $form->input('phone_num');
echo $form->input('device_id');
echo $form->end('Create');
echo $form->create('Subject', array('action' => 'index'));
echo $form->end('Cancel');
?>