<?php
/*****************************************************************************
 * views/branches/editbranch.ctp                                             *
 *                                                                           *
 * Page to edit a branch.                                                    *
 *****************************************************************************/

echo $this->Session->flash();

if (isset($result))
{
	if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showbranches/$questionid"),
			array('async' => true, 'update' => '#branches')
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}
echo $form->create('Branch', array('url' => "editbranch/$branchid", 'default' => false));
echo $form->input('next_q', array('value' => $next_q));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
echo $this->Js->submit('Edit', array('action' => "editbranch/$branchid", 'update' => '#b_space'));
echo $form->end();
echo $form->create('Branch', array('default' => false));
echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
echo $form->input('confirm', array('type' => 'hidden', 'value' => false));
echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
echo $this->Js->submit('Cancel', array('action' => 'editbranch', 'update' => '#b_space'));
echo $form->end();

echo $this->Js->writeBuffer();
?>