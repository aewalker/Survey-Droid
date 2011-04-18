<?php
/*****************************************************************************
 * views/questions/addquestion.ctp                                           *
 *                                                                           *
 * add a new question.                                                       *
 *****************************************************************************/

echo $this->Session->flash();

if (isset($result))
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
echo $form->create('Question', array('action' => "addquestion/$surveyid", 'default' => false));
echo $form->input('q_text');
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array('type' => 'hidden', 'value' => $surveyid));
echo $this->Js->submit('Add', array('action' => "addquestion/$surveyid", 'update' => '#q_space'));
echo $form->end();
echo $form->create('Question', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $this->Js->submit('Cancel', array('action' => "addquestion/$surveyid", 'update' => '#q_space'));
echo $form->end();

echo $this->Js->writeBuffer();
?>