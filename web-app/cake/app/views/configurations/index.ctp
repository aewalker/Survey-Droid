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
echo $form->create(false, array('action' => 'index'));
echo $form->input('confirm', array('type' => 'hidden', 'value' => 'true'));
?>
<h4>General</h4>
<?php
//featues enable/disable
echo $form->input('features_enabled.location', array('type' => 'checkbox', 'checked' => $data['features_enabled']['location'], 'label' => 'Location tracking enabled'));
echo $form->input('features_enabled.callog', array('type' => 'checkbox', 'checked' => $data['features_enabled']['callog'], 'label' => 'Call/text logging enabled'));
echo $form->input('features_enabled.survey', array('type' => 'checkbox', 'checked' => $data['features_enabled']['survey'], 'label' => 'Surveys enabled'));
//admin name
echo $form->input('admin_name', array('value' => $data['admin_name'], 'label' => 'Study contact\'s name'));
//admin phone number
echo $form->input('admin_phone_number', array('value' => $data['admin_phone_number'], 'label' => 'Study contact\'s phone number (numbers only)')); 
?>
<h4>Surveys</h4>
<?php
//allow blank free response
echo $form->input('allow_blank_free_response', array('type' => 'checkbox', 'checked' => $data['allow_blank_free_response'], 'label' => 'Allow free response questions to accept blank answers'));
//allow no choices in multiple choice
echo $form->input('allow_no_choices', array('type' => 'checkbox', 'checked'  => $data['allow_no_choices'], 'label' => 'Allow multi choice questions to be answered with no choices'));
//show survey names
echo $form->input('show_survey_name', array('type' => 'checkbox', 'checked'  => $data['show_survey_name'], 'label' => 'Show survey names in app'));
?>
<h4>Tracking</h4>
<?php
//locations tracked
echo $form->input('location_tracked.0.long', array('value' => $data['location_tracked'][0]['long'], 'label' => 'Tracking area center longitude'));
echo $form->input('location_tracked.0.lat', array('value' => $data['location_tracked'][0]['lat'], 'label' => 'Tracking area center latitude'));
echo $form->input('location_tracked.0.radius', array('value' => $data['location_tracked'][0]['radius'], 'label' => 'Tracking area radius (in kilometers)'));
//times tracked
echo $form->input('time_tracked.0.start', array('value' => $data['time_tracked'][0]['start'], 'label' => 'Start of time tracked (hhmm)'));
echo $form->input('time_tracked.0.end', array('value' => $data['time_tracked'][0]['end'], 'label' => 'End of time tracked (hhmm)'));
//location interval
echo $form->input('location_interval', array('value' => $data['location_interval'], 'label' => 'How often (in minutes) should location information be collected'));
?>
<h4>Security</h4>
<?php
//use https
echo $form->input('https', array('type' => 'checkbox', 'checked'  => $data['https'], 'label' => 'Use secure (HTTPS) transmission'));
?>
<h4>Technical</h4>
<?php
//pull interval
echo $form->input('pull_interval', array('value' => $data['pull_interval'], 'label' => 'Pull interval (in minutes)'));
//push interval
echo $form->input('push_interval', array('value' => $data['push_interval'], 'label' => 'Push interval (in minutes)'));
//scheduler interval
echo $form->input('scheduler_interval', array('value' => $data['scheduler_interval'], 'label' => 'Survey scheduler interval (in minutes)'));
//server ip/domain name
echo $form->input('server', array('value' => $data['server'], 'label' => 'Server name/ip (WARNING: DO NOT CHANGE UNLESS YOU KNOW WHAT YOU\'RE DOING)'));
//voice recording format
echo $form->input('voice_format', array('type' => 'select', 'options' => array('mpeg4' => 'mpeg4', '3gp' => '3gp'), 'value' => $data['voice_format'], 'label' => 'Format for recored audio'));
echo $form->end('Save');
echo $form->create(false, array('url' => '/'));
echo $form->end('Cancel');
?>