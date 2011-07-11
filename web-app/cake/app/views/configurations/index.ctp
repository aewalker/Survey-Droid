<?php
/*---------------------------------------------------------------------------*
 * views/configurations/index.ctp                                            *
 *                                                                           *
 * Settings view/edit page.                                                  *
 *---------------------------------------------------------------------------*/
echo $this->Session->flash();
?>
<h3>Welcome to the settings configuration page.</h3>
<p>Here, you can view and modify the way that many different application features work on active phones.  Just edit the settings on this page and press "Save" when you are done.  If you navigate away from this page without saving, then the previous settings will be preserved.</p>
<?php
echo $form->create('Configuration', array('action' => 'index'));
echo $form->input('confirm', array('type' => 'hidden', 'value' => 'true'));

print_r($data);
?>
<h4>General</h4>
<?php
//featues enable/disable
//admin name
//admin phone number
?>
<h4>Surveys</h4>
<?php
//allow blank free response
//allow no choices in multiple choice
//show survey names
?>
<h4>Tracking</h4>
<?php
//locatios tracked
//times tracked
//location interval
?>
<h4>Security</h4>
<?php
//use https
?>
<h4>Technical</h4>
<?php
//push interval
//pull interval
//scheduler interval
//server ip/domain name
//voice recording format
echo $form->end('Save');
echo $form->create(false, array('url' => '/'));
echo $form->end('Cancel');
?>