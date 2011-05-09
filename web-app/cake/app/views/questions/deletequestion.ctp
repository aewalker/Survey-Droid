<?php
/*---------------------------------------------------------------------------*
 * views/questions/deletequestion.ctp                                        *
 *                                                                           *
 * Page to delete a question.                                                *
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
	'url' => "deletequestion/$questionid",
	'default' => false));
echo '<p>Are you sure you want to delete this question? '
	.'This action cannot be undone.</p>';
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('survey_id', array(
	'type' => 'hidden',
	'value' => $surveyid));
echo $form->input('id', array('type' => 'hidden', 'value' => $questionid));
echo $this->Js->submit('Delete', array(
	'action' => "deletequestion/$questionid",
	'update' => "#question_space_$questionid"));
echo $form->end();

//cancel button
echo $form->create('Question', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $this->Js->submit('Cancel', array(
	'action' => 'deletequestion',
	'update' => "#question_space_$questionid"));
echo $form->end();

echo $this->Js->writeBuffer();
?>