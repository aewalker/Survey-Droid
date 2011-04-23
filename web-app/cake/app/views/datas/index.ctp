<?php
/*****************************************************************************
 * views/data/index.ctp                                                    *
 *                                                                           *
 * Data overview page.                                                     *
 *****************************************************************************/
echo $this->Session->flash();

echo $html->link('Location summary (subject based)', array('controller' => 'datas', 'action'=>'locations'));
echo '<br/><br/>';

echo $html->link('Call summary (subject based)', array('controller' => 'datas', 'action'=>'calls'));
echo '<br/><br/>';

echo $html->link("Subject's Status summary (subject based)", array('controller' => 'datas', 'action'=>'subjectstatuses'));
echo '<br/><br/><br/>';

echo 'Select a survey to see <b>Answers to Questions</b>';
echo $table->startTable('Survey');
echo $table->tableBody($results, array(
			'Select' => array( 'command' => '../datas/answers', 'arg' => 'id', 'type' => 'link' ) 
			),
			array('id', 'name')
		);
echo $table->endTable(array(' '=>' '));
?>