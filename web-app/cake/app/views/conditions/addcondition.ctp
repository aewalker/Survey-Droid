<?php
/*****************************************************************************
 * views/conditions/addcondition.ctp                                         *
 *                                                                           *
 * add a new condition.                                                      *
 *****************************************************************************/
if(isset($result))
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
 else
 {
	echo $form->create('Condition', array('action' => "addcondition/$branchid", 'default' => false));
	echo $form->input('question_id', array('type' => 'text'));
	echo $form->input('type', array('type' => 'select', 'options' => array('just was', 'ever was', 'never has been')));
	echo $form->input('choice_id', array('type' => 'text'));
	echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
	echo $form->input('branch_id', array('type' => 'hidden', 'value' => $branchid));
	echo $this->Js->submit('Add', array('action' => "addcondition/$branchid", 'update' => "#branch_conditions_space_$branchid"));
	echo $form->end();
	echo $form->create('Condition', array('default' => false));
	echo $form->input('cancel', array('type' => 'hidden', 'value' => true));
	echo $this->Js->submit('Cancel', array('action' => "addcondition/$branchid", 'update' => "#branch_conditions_space_$branchid"));
	echo $form->end();

	echo $this->Js->writeBuffer();
}
?>