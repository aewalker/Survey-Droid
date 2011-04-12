<?php
/*****************************************************************************
 * views/choices/deletechoice.ctp                                            *
 *                                                                           *
 * Page to delete a choice.                                                  *
 *****************************************************************************/
 echo $this->Session->flash();

if (isset($result))
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showchoices/$questionid"),
			array('async' => true, 'update' => '#choices')
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}
echo $form->create('Choice', array('url' => "deletechoice/$id", 'default' => false));
echo '<p>Are you sure you want to delete this choice?  This action cannot be undone.</p>';
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
echo $form->input('id', array('type' => 'hidden', 'value' => $id));
echo $this->Js->submit('Delete', array('action' => "deletechoice/$id", 'update' => '#ch_space'));
echo $form->end();
echo $form->create('Choice', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
echo $this->Js->submit('Cancel', array('action' => 'deletechoice', 'update' => '#ch_space'));
echo $form->end();

echo $this->Js->writeBuffer();

?>