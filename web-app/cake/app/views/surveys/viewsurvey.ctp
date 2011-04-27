<?php
/*****************************************************************************
 * views/survey/viewsurvey.ctp                                               *
 *                                                                           *
 * Main survey page; shows one survey in detail via AJAX.                    *
 *****************************************************************************/
echo $this->Session->flash();

echo "<h2>Survey \"$surveyname\"</h2>";
?>
<div id="questions"></div>
<div id="questions_space"></div>
<?php

echo '<script>'.$this->Js->request(array
(
'controller' => 'questions',
'action' => "showquestions/$surveyid"),
array('async' => true, 'update' => '#questions')
).'</script>';
?>