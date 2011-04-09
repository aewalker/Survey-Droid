<?php
/*****************************************************************************
 * views/survey/addsurvey.ctp                                                *
 *                                                                           *
 * add a new survey.                                                         *
 *****************************************************************************/
echo $form->create('Survey', array('action' => 'addsurvey'));
echo $form->input('name');
echo $form->end('Add');
echo $form->create('Survey', array('action' => 'index'));
echo $form->end('Cancel');
?>