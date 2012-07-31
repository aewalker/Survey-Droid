<?php
/*---------------------------------------------------------------------------*
 * views/errors/missing_controller.ctp                                       *
 *                                                                           *
 * Page to render when the requested page references a non-existant          *
 * controller.                                                               *
 *---------------------------------------------------------------------------*/
?>
<h3>That page does not exist!</h3>
<p>
If you recieved this message after following a link, please report it to
your system administrator.
</p>
<code>Missing controller: <?php echo $controller; ?></code>
