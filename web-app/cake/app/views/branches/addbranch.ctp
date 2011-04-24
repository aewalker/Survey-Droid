<?php
/*****************************************************************************
 * views/branches/addbranch.ctp                                              *
 *                                                                           *
 * add a new branch.                                                         *
 *****************************************************************************/
 if(isset($result))
 {
	 if ($result == true)
	{
		echo '<script>'.$this->Js->request(array
		(
			'action' => "showbranches/$questionid"),
			array('async' => true, 'update' => "#question_branches_$questionid")
		).'</script>';
		echo $this->Js->writeBuffer();
		return;
	}
	echo '<h3>There were errors</h3>';
}
else
{
	echo $form->create('Branch', array('action' => "addbranch/$questionid", 'default' => false));
	echo $form->input('next_q');
	echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
	echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
	echo $this->Js->submit('Add', array('action' => "addbranch/$questionid", 'update' => "#question_branches_space_$questionid"));
	echo $form->end();
	echo $form->create('Branch', array('default' => false));
	echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
	echo $this->Js->submit('Cancel', array('action' => "addbranch/$questionid", 'update' => "#question_branches_space_$questionid"));
	echo $form->end();

	echo $this->Js->writeBuffer();
}
?>