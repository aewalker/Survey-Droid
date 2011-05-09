<?php
/*---------------------------------------------------------------------------*
 * views/errors/missing_action.ctp                                           *
 *                                                                           *
 * Page to render when the requested page references a non-existant view.    *                                                              *
 *---------------------------------------------------------------------------*/
?>
<h3>That page does not exist!</h3>
<p>
If you recieved this message after following a link, please report it to
your system administrator.
</p>
<code>Missing action: <?php echo $action; ?></code>