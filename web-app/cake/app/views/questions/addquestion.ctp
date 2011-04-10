<?php
/*****************************************************************************
 * views/questions/addquestion.ctp                                                *
 *                                                                           *
 * add a new question.                                                         *
 *****************************************************************************/
echo $form->create('Question', array('action' => "showquestions/$surveyid"));
echo $form->input('q_text');
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array('type' => 'hidden', 'value' => $surveyid));
echo $form->input('surveyid', array('type' => 'hidden', 'value' => $surveyid));
echo $form->end('Add');
echo $form->create('Question', array('action' => "showquestions/$surveyid"));
echo $form->end('Cancel');
?>