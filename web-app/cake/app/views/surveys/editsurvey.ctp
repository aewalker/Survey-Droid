<?php
/*****************************************************************************
 * views/survey/editsurvey.ctp                                               *
 *                                                                           *
 * Page to edit a survey's info.                                             *
 *****************************************************************************/
echo $form->create('Survey', array('url' => "/surveys/editsurvey/$surveyid"));
echo $form->input('name', array('value' => $name));
echo $form->input('question_id', array('value' => $question_id));
echo $form->input('mo', array('value' => $mo, 'label' => 'Monday'));
echo $form->input('tu', array('value' => $tu, 'label' => 'Tuesday'));
echo $form->input('we', array('value' => $we, 'label' => 'Wednesday'));
echo $form->input('th', array('value' => $th, 'label' => 'Thursday'));
echo $form->input('fr', array('value' => $fr, 'label' => 'Friday'));
echo $form->input('sa', array('value' => $sa, 'label' => 'Saturday'));
echo $form->input('su', array('value' => $su, 'label' => 'Sunday'));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->end('Edit');
echo $form->create('Survey', array('action' => 'index'));
echo $form->end('Cancel');
?>