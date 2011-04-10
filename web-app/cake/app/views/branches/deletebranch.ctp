<?php
/*****************************************************************************
 * views/branches/deletebranch.ctp                                             *
 *                                                                           *
 * Page to delete a branch.                                                  *
 *****************************************************************************/
echo $this->Session->flash();
echo $form->create('Branch');
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo "<p>Are you sure you want to delete the branch pointing to $next_q?</p>";
echo '<p><strong>This action cannot be undone.</strong></p>';
echo $form->end('Yes');
echo $form->create('Branch');
echo $form->end('No');
?>