<?php
/*****************************************************************************
 * views/branches/addbranch.ctp                                                *
 *                                                                           *
 * add a new branch.                                                         *
 *****************************************************************************/
 if(isset($result) && $result==true)
 {
 }
 else
 {
	echo $form->create('Branch', array('action' => "addbranch/$questionid"));
	echo $form->input('next_q');
	echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
	echo $form->input('question_id', array('type' => 'hidden', 'value' => $questionid));
	echo $form->input('questionid', array('type' => 'hidden', 'value' => $questionid));
	echo $form->end('Add');
	echo $form->create('Branch', array('action' => "../questions/showquestions/$questionid"));
	echo $form->end('Cancel');
}
?>