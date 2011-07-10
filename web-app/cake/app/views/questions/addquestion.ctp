<?php
/*---------------------------------------------------------------------------*
 * views/questions/addquestion.ctp                                           *
 *                                                                           *
 * add a new question.                                                       *
 *---------------------------------------------------------------------------*/

echo $this->Session->flash();

if (isset($result)) //if result is set, then the user has already submitted
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showquestions/$surveyid"),
			array('async' => true, 'update' => '#questions')
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}

//main form
echo $form->create('Question', array(
	'enctype' => 'multipart/form-data', //to allow file uploads
	'action' => "addquestion/$surveyid",
	'default' => false));
echo $form->input('q_text', array('label' => 'Question Text'));
echo $form->input('q_type', array('label' => 'Question Type', 'type' => 'select', 'options' => array
(
	QT_SINGLE_CHOICE => 'Sinlge Choice',
	QT_MULTI_CHOICE => 'Multi Choice',
	QT_SCALE_TEXT => 'Scale (text)',
	QT_SCALE_IMG => 'Scale (images)',
	QT_FREE_RESPONSE => 'Free Response'
)));
//TODO use some jQuery to only show these if the proper type is selected
echo $form->input('q_text_low', array('label' => 'Low-end Text (for text scale only)'));
echo $form->input('q_text_high', array('label' => 'High-end Text (for text scale only)'));
echo $form->input('q_img_low', array('type' => 'file', 'label' => 'Low-end Image (for image scale only)'));
echo $form->input('q_img_high', array('type' => 'file', 'label' => 'High-end Image (for image scale only)'));

echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array('type' => 'hidden', 'value' => $surveyid));
echo $this->Js->submit('Add', array(
	'action' => "addquestion/$surveyid",
	'update' => '#questions_space'));
echo $form->end();

//cancel button
echo $form->create('Question', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $this->Js->submit('Cancel', array(
	'action' => "addquestion/$surveyid",
	'update' => '#questions_space'));
echo $form->end();

echo $this->Js->writeBuffer();
?>