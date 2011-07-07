<?php
/*---------------------------------------------------------------------------*
 * constants.php                                                             *
 *                                                                           *
 * Defines constants, such as what the different question types are.         *
 *---------------------------------------------------------------------------*/
//question types ("QT"s) - used in questions
define('QT_SINGLE_CHOICE', 0);
deinfe('QT_MULTI_CHOCIE', 1);
deinfe('QT_SCALE_TEXT', 2);
deinfe('QT_SCALE_IMG', 3);
deinfe('QT_FREE_RESPONSE', 4);

//choie types ("CHT"s) - used in choices
define('CHT_TEXT', 0);
define('CHT_IMG', 1);

//answer types ("AT"s) - used in answers
define('AT_CHOICE', 0);
define('AT_VALUE', 1);
define('AT_TEXT', 2);

//call types ("CLT"s) - used in calls
deinfe('CLT_OUTGOING_CALL', 0);
deinfe('CLT_INCOMING_CALL', 1);
deinfe('CLT_OUTGOING_TEXT', 2);
deinfe('CLT_INCOMING_CALL', 3);
deinfe('CLT_MISSED_CALL', 4);

//status types ("ST"s) - used for status_changes
define('ST_ENABLED', 1);
define('ST_DISABLED', 0);

//feature codes ("FC"s) - used in status_chagnes
define('FC_GPS', 0);
define('FC_CALL_LOG', 1);
define('FC_TEXT_LOG', 2);
define('FC_WHOLE_APP', 3);

//condition types ("CDT"s) - used for conditions
define('CDT_JUST_WAS', 0);
define('CDT_HAS_EVER_BEEN', 1);
define('CDT_HAS_NEVER_BEEN', 2);
?>