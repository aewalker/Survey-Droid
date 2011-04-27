<?php
/*****************************************************************************
 * views/data/index.ctp                                                    *
 *                                                                           *
 * Data overview page.                                                     *
 *****************************************************************************/
echo $this->Session->flash();

echo '<div id="links">'.$html->link('Location summary (subject based)', array('controller' => 'datas', 'action'=>'locations'));
echo '</div>';

echo '<div id="links">'.$html->link('Call summary (subject based)', array('controller' => 'datas', 'action'=>'calls'));
echo '</div>';

echo '<div id="links">'.$html->link("Subject's Status summary (subject based)", array('controller' => 'datas', 'action'=>'subjectstatuses'));
echo '</div><br/>';

echo 'Select a survey to see <b>Answers to Questions</b>';
echo $table->startTable('Survey');
echo $table->tableBody($results, array(
			'Select' => array( 'command' => '../datas/answers', 'arg' => 'id', 'type' => 'link' ) 
			),
			array('id', 'name')
		);
echo $table->endTable(array(' '=>' '));
?>