<?php
/*---------------------------------------------------------------------------*
 * views/survey/addsurvey.ctp                                                *
 *                                                                           *
 * add a new survey.                                                         *
 *---------------------------------------------------------------------------*/
echo $form->create('Survey', array('action' => 'addsurvey'));
echo $form->input('name');
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
//TODO it would be good to automatically populate this field somehow...
//echo $form->input('question_id', array('type' => 'hidden', 'value' => 1));
echo $form->end('Add');
echo $form->create('Survey', array('action' => 'index'));
echo $form->end('Cancel');
?>