<?php
/*---------------------------------------------------------------------------*
 * views/survey/editsurvey.ctp                                               *
 *                                                                           *
 * Page to edit a survey's info.                                             *
 *---------------------------------------------------------------------------*/
echo $form->create('Survey', array('url' => "editsurvey/$surveyid"));
echo $form->input('name', array('value' => $name));
echo $form->input('question_id', array('value' => $questionid,
	'label' => 'First Question', 'type' => 'text'));
echo '<p>You may wish to allow phone users to start this survey whenever they want to. '.
	'To enable this feature for this survey, check the box below.';
echo $form->input('subject_init', array
(
	'type' => 'checkbox',
	'checked' => $subject_init,
	'label' => 'Allow subject initiation'
));
echo '<p>To set when this survey should be administered to subjects, enter'.
	' 4 digit numbers in the fiends below separated by commas.  For exmple, '.
	'to set a survey to be administered on Mondays at 11:00 AM and 3:45 PM, '.
	'enter "1100,1545" in the "Monday" field.';
echo $form->input('mo', array('value' => $days['mo'], 'label' => 'Monday'));
echo $form->input('tu', array('value' => $days['tu'], 'label' => 'Tuesday'));
echo $form->input('we', array('value' => $days['we'], 'label' => 'Wednesday'));
echo $form->input('th', array('value' => $days['th'], 'label' => 'Thursday'));
echo $form->input('fr', array('value' => $days['fr'], 'label' => 'Friday'));
echo $form->input('sa', array('value' => $days['sa'], 'label' => 'Saturday'));
echo $form->input('su', array('value' => $days['su'], 'label' => 'Sunday'));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->end('Edit');

echo $form->create('Survey', array('action' => 'index'));
echo $form->end('Cancel');
?>