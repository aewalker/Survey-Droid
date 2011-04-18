<?php
/*****************************************************************************
 * views/conditions/showconditions.ctp                                       *
 *                                                                           *
 * Branches of a question.                                                   *
 *****************************************************************************/
 echo $this->Session->flash();

//show the results
echo '<br/><br/>';
echo $table->startTable('Condition');

//change the types to user-readable versions
foreach ($results as &$result)
{
	if ($result['Condition']['type'] == 0) $result['Condition']['type'] = 'just was';
	else if ($result['Condition']['type'] == 1) $result['Condition']['type'] = 'ever was';
	else if ($result['Condition']['type'] == 2) $result['Condition']['type'] = 'never has been';
}

echo $table->tableBody($results, array(
            'Edit' => array(
                  'command' => '/conditions/editcondition', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#con_space'),
            'Delete' => array(
                  'command' => '/conditions/deletecondition', 'arg' => 'id', 'type' => 'ajax', 'update'=> '#con_space'),
            ));
		
echo $table->endTable(array('Add Condition' => array('command' => "/conditions/addcondition", 'arg' =>$branchid, 
					'type' => 'ajax',
					'update'=>'#con_space')));
                   
echo $this->Js->writeBuffer();

?>
