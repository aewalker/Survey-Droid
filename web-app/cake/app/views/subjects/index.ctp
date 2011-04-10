<?php
/*****************************************************************************
 * views/subject/index.ctp                                                   *
 *                                                                           *
 * Subjects overview page.                                                   *
 *****************************************************************************/
echo $this->Session->flash();

//show the search form
echo $form->create('Subject', array('action' => 'index'));
echo $form->input('first_name');
echo $form->input('last_name');
echo $form->end('Search');

//show the results
echo $table->startTable('Subject');
echo $table->tableBody($results);
echo $table->endTable(array('Add Subject' => array('command' => 'add', 'type' => 'link', 'arg' => NULL)));
?>