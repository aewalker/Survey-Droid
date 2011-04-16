<?php
/*****************************************************************************
 * views/questions/editquestion.ctp                                          *
 *                                                                           *
 * Page to edit a question.                                                  *
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
echo $form->create('Question', array('url' => "editquestion/$questionid", 'default' => false));
echo $form->input('q_text', array('value' => $q_text));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array('type' => 'hidden', 'value' => $surveyid));
echo $form->input('id', array('type' => 'hidden', 'value' => $questionid));
echo $this->Js->submit('Edit', array('action' => "editquestion/$questionid", 'update' => '#q_space'));
echo $form->end();
echo $form->create('Question', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $this->Js->submit('Cancel', array('action' => 'editquestion', 'update' => '#q_space'));
echo $form->end();

echo $this->Js->writeBuffer();
?>