<?php
/*---------------------------------------------------------------------------*
 * config.php                                                                *
 *                                                                           *
 * This is the main configuration file for the Survey Droid web site.  The   *
 * intent of this file is to centralize all non-essential configuration      *
 * settings.  This file is automatically loaded each time a request is made. *
 *---------------------------------------------------------------------------*/
/**
 * @var SSL - set this to true to use https on all pages
 * 
 * Your server must have a valid SSL certificate in order to enable this.  In
 * addition, you must configure your web server correctly to support https
 * connections.
 * 
 * However, if you do not enable this feature, you should be aware that all
 * server traffic will be visible to anyone who is listening in.  Because of
 * this, it is HIGHLY reccomended that you enable this option.
 * 
 * Even if this is set to true, one URL, /api/error, will still allow
 * unencrypted trafic. This is to allow the phone application to report errors.
 */
define('SSL', false);
//Configure::write('Routing.prefixes', array('rest'));

/**
 * @var NUM_ADMIN_EMAILS - set the admin's email address
 * 
 * If you are using a version of the Survey Droid phone app that has error
 * reporting on, you can set the email address(es) that the reports are sent
 * to.  Set NUM_ADMIN_EMAILS to be the number of addresses that are to be
 * mailed to, and then set variables called 'ADMIN_EMAILX' where X is a
 * number starting at 1 to be the addresses.
 */
define('NUM_ADMIN_EMAILS', 2);
define('ADMIN_EMAIL1', 'austinewalker@gmail.com');
define('ADMIN_EMAIL2', 'nsugie@sbcglobal.net');

?>
