<?php
/*****************************************************************************
 * views/survey/viewsurvey.ctp                                               *
 *                                                                           *
 * Main survey page; shows one survey in detail via AJAX.                    *
 *****************************************************************************/
echo $this->Session->flash();

echo "<h2>Survey \"$surveyname\"</h2>";

echo '<div id="questions"></div>';
echo '<div id="questions_space"></div>';
echo '<div id="choices"></div>';
echo '<div id="choices_space"></div>';
echo '<div id="branches"></div>';
echo '<div id="brances_space"></div>';
echo '<div id="conditions"></div>';
echo '<div id="conitions_space"></div>';

echo '<script>'.$this->Js->request(array
(
'controller' => 'questions',
'action' => "showquestions/$surveyid"),
array('async' => true, 'update' => '#questions')
).'</script>';
?>