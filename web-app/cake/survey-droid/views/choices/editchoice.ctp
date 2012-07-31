<?php
/*---------------------------------------------------------------------------*
 * views/choices/editchoice.ctp                                              *
 *                                                                           *
 * Page to edit a choice.                                                    *
 *---------------------------------------------------------------------------*/

echo $this->Session->flash();

if (isset($result)) //if result is set, then the user has already submitted
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showchoices/$questionid"),
			array('async' => true, 'update' => "#question_choices_$questionid")
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}

//main form
echo $form->create('Choice', array(
	'url' => "editchoice/$choiceid",
	'default' => false));
echo $form->input('choice_text', array('value' => $choice_text));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array(
	'type' => 'hidden',
	'value' => $questionid));
echo $this->Js->submit('Edit', array(
	'action' => "editchoice/$choiceid",
	'update' => "#choice_space_$choiceid"));
echo $form->end();

//cancel button
echo $form->create('Choice', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $form->input('confirm', array('type' => 'hidden', 'value' => false));
echo $form->input('question_id', array(
	'type' => 'hidden',
	'value' => $questionid));
echo $this->Js->submit('Cancel', array(
	'action' => 'editchoice',
	'update' => "#choice_space_$choiceid"));
echo $form->end();

echo $this->Js->writeBuffer();
?>