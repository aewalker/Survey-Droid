<?php
/*---------------------------------------------------------------------------*
 * config.php                                                                *
 *                                                                           *
 * This is the main configuration file for the PEOPLES web site.  The intent *
 * of this file is to centralize all non-essential configuration settings.   *
 * This file is automatically loaded each time a request is made.            *
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
 * this, it is HIGHLY reccomended that you enable this option.  It is left
 * disabled by default in order to support as many different setups as
 * possible by default.
 */
define('SSL', false);
?>