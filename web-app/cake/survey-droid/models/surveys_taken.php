<?php
/*---------------------------------------------------------------------------*
 * models/surveys_taken.php                                                  *
 *                                                                           *
 * Model for data about a subjects taking surveys                            *
 *---------------------------------------------------------------------------*/
/**
 * Tracks when surveys are taken.
 * 
 * @author Tony Xiao
 */
class SurveysTaken extends AppModel
{
	//for php4
	var $name = 'SurveysTaken';
	
	var $belongsTo = 'Survey';
}
?>