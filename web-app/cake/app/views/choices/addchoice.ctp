<?php
/*****************************************************************************
 * views/choices/addchoice.ctp                                               *
 *                                                                           *
 * add a new choice.                                                         *
 *****************************************************************************/
 if(isset($result))
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
 else
 {
	echo $form->create('Choice', array('action' => "addchoice/$questionid", 'default' => false));
	echo $form->input('choice_text');
	echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
	echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
	echo $this->Js->submit('Add', array('action' => "addchoice/$questionid", 'update' => "#question_choices_space_$questionid"));
	echo $form->end();
	echo $form->create('Choice', array('default' => false));
	echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
	echo $this->Js->submit('Cancel', array('action' => "addchoice/$questionid", 'update' => "#question_choices_space_$questionid"));
	echo $form->end();

	echo $this->Js->writeBuffer();

}


?>