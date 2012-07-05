<?php
/*---------------------------------------------------------------------------*
 * 301rewriting.php                                                          *
 *                                                                           *
 * Redirects things like survey-droid.org/index.html to survey-droid.org/    *
 * with a Google-friendly 301 code.                                          *
 *---------------------------------------------------------------------------*/
$url = $_SERVER['REQUEST_URI'];
if (check301($url))
{
	header('HTTP/1.1 301 Moved Permanently');
	header("Location: $url");
	exit();
}

//check for all the common things
function check301(&$url)
{
	//common URL suffixes to check for
	$suffixes = array
	(
		'index.php',
		'index.htm',
		'index.html',
		'index.shtml',
		'index.asp',
		'default.asp',
		'index.aspx',
		'index.cfm',
		'index.pl',
		'default.htm',
		'index.py',
		'index.jsp',
		'index.psp'
	);
	$returnVal = false;
	
	foreach ($suffixes as $suffix)
	{
		if (substr($url, strlen($suffix) * -1) == $suffix)
		{
			$url = substr($url, 0, strlen($suffix) * -1);
			$returnVal = true;
		}
	}
	return $returnVal;
}
?>
