<?php
/*---------------------------------------------------------------------------*
 * views/branches/deletebranch.ctp                                           *
 *                                                                           *
 * Page to delete a branch.                                                  *
 *---------------------------------------------------------------------------*/
 echo $this->Session->flash();

if (isset($result)) //if result is set, then the user has already submitted
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showbranches/$questionid"), array(
				'async' => true,
				'update' => "#question_branches_$questionid")
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
	return;
}

//main form
echo $form->create('Branch', array(
	'url' => "deletebranch/$id",
	'default' => false));
echo '<p>Are you sure you want to delete'
	."this branch pointing to question $next_q? ";
echo 'This action cannot be undone.</p>';
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array(
	'type' => 'hidden',
	'value' => $questionid));
echo $form->input('id', array('type' => 'hidden', 'value' => $id));
echo $this->Js->submit('Delete', array(
	'action' => "deletebranch/$id",
	'update' => "#branch_space_$id"));
echo $form->end();

//cancel button
echo $form->create('Branch', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array(
	'type' => 'hidden',
	'value' => $questionid));
echo $this->Js->submit('Cancel', array(
	'action' => 'deletebranch',
	'update' => "#branch_space_$id"));
echo $form->end();

echo $this->Js->writeBuffer();

?>