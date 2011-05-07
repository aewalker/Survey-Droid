<?php
/*---------------------------------------------------------------------------*
 * views/answers/pull.ctp                                                    *
 *                                                                           *
 * Lets phones pull survey data from the webserver.                          *
 *---------------------------------------------------------------------------*/
echo $this->Js->value($results); //convert to JSON and strip whitespace
?>