<?php
/*****************************************************************************
 * views/branches/deletebranch.ctp                                           *
 *                                                                           *
 * Page to delete a branch.                                                  *
 *****************************************************************************/
 echo $this->Session->flash();

if (isset($result))
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showconditions/$branchid"),
			array('async' => true, 'update' => "#branch_conditions_$branchid")
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}
echo $form->create('Condition', array('url' => "deletecondition/$id", 'default' => false));
echo '<p>Are you sure you want to delete this condition?  This action cannot be undone.</p>';
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('branch_id', array('type' => 'hidden', 'value' => $branchid));
echo $form->input('id', array('type' => 'hidden', 'value' => $id));
echo $this->Js->submit('Delete', array('action' => "deletecondition/$id", 'update' => "#condition_space_$id"));
echo $form->end();
echo $form->create('Condition', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $form->input('branch_id', array('type' => 'hidden', 'value' => $branchid));
echo $this->Js->submit('Cancel', array('action' => 'deletecondition', 'update' => "#condition_space_$id"));
echo $form->end();

echo $this->Js->writeBuffer();

?>