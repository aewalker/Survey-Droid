<?php
/*---------------------------------------------------------------------------*
 * views/answers/pull.ctp                                                    *
 *                                                                           *
 * Lets phones pull survey data from the webserver.                          *
 *---------------------------------------------------------------------------*/
//convert to JSON and strip whitespace
if ($result === true) echo $this->Js->value($results);
else
{
	$this->error(400, 'Bad Request', "failure: $message");
	exit;
}
?>