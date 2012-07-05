<?php
/*---------------------------------------------------------------------------*
 * views/answers/salt.ctp                                                    *
 *                                                                           *
 * Lets phones get the salt string from the server.                          *
 *---------------------------------------------------------------------------*/
if ($result === false)
{
	$this->error(400, 'Bad Request', "failure: $message");
	exit;
}
echo $result;
?>