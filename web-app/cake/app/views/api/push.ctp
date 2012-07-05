<?php
/*---------------------------------------------------------------------------*
 * views/answers/push.ctp                                                    *
 *                                                                           *
 * Lets phones push survey answers, GPS data, call logs, and staus info to   *
 * the web server.                                                           *
 *---------------------------------------------------------------------------*/
if ($result === true) echo 'success';
else
{
	$this->error(400, 'Bad Request', "failure: $message");
	exit;
}
?>