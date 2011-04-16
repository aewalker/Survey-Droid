<?php
/*****************************************************************************
 * views/survey/deletesurvey.ctp                                             *
 *                                                                           *
 * Page to delete a survey.                                                  *
 *****************************************************************************/
echo $this->Session->flash();
echo $form->create('Survey', array('url' => "/surveys/deletesurvey/$id"));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo "<p>Are you sure you want to delete $name?</p>";
echo '<p><strong>This action cannot be undone.</strong></p>';
echo $form->end('Yes');
echo $form->create('Survey', array('action' => 'index'));
echo $form->end('No');
?>