<?php
/*---------------------------------------------------------------------------*
 * views/answers/error.ctp                                                   *
 *                                                                           *
 * Used when phones need to report an unhandled exception to the system.     *
 *---------------------------------------------------------------------------*/
if ($result === false)
{
	$this->error(400, 'Bad Request', "failure: $message");
	exit;
}
?>