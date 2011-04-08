<?php
/*****************************************************************************
 * views/subject/edit.ctp                                                    *
 *                                                                           *
 * Page to edit a subject's info.                                            *
 *****************************************************************************/
echo $form->create('Subject', array('url' => "/subjects/edit/$id"));
echo $form->input('first_name', array('value' => $first_name));
echo $form->input('last_name', array('value' => $last_name));
echo $form->input('phone_num', array('value' => $phone_num));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->end('Edit');
echo $form->create('Subject', array('action' => 'index'));
echo $form->end('Cancel');
?>