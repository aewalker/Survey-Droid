<?php
/*****************************************************************************
 * views/questions/editquestion.ctp                                               *
 *                                                                           *
 * Page to edit a question.                                             *
 *****************************************************************************/
 
 echo $this->Session->flash();
 
echo $form->create('Question', array('url' => "editquestion/$questionid"));
echo $form->input('q_text', array('value' => $q_text));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array('type' => 'hidden', 'value' => $surveyid));
echo $form->input('id', array('type' => 'hidden', 'value' => $questionid));
echo $form->end('Edit');
echo $form->create('Question', array('action' => "showquestions/$surveyid"));
echo $form->end('Cancel');
?>