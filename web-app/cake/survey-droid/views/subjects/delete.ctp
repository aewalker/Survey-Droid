<?php
/*---------------------------------------------------------------------------*
 * views/subject/delete.ctp                                                  *
 *                                                                           *
 * Page to delete a subject.                                                 *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();

echo $form->create('Subject', array('url' => "/subjects/delete/$id"));
echo $form->input('confirm', array('type' => 'hidden', 'value' => true));
echo "<p>Are you sure you want to delete $first_name $last_name?</p>";
echo '<p><strong>This action cannot be undone.</strong></p>';
echo $form->end('Yes');

echo $form->create('Subject', array('action' => 'index'));
echo $form->end('No');
?>