Ext.define("SD.view.setting.Tab", {
    extend: "Ext.panel.Panel",
    alias: 'widget.settingsTab',
    title: 'Settings',
    items: [{
        itemId: 'settingsForm',
        xtype: 'form',
        autoScroll: true,
        buttonAlign: 'left',
        buttons: [{
            text: 'Save',
            action: 'save'
        }],
        items: [
            {
                margin: '5',
                xtype: 'fieldset',
                title:'General',
                defaults: {
                    anchor: '60%',
                    labelWidth: 400
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: 'Location Tracking Enabled',
                        name: 'features_enabled.location',
                        xtype: 'checkbox'
                    },{
                        fieldLabel: 'Call / Text Logging Enabled',
                        name: 'features_enabled.callog',
                        xtype: 'checkbox'
                    },{
                        fieldLabel: 'Surveys Enabled',
                        name: 'features_enabled.survey',
                        xtype: 'checkbox'
                    },{
                        fieldLabel: 'Study Contact\'s name',
                        name: 'admin_name'
                    },{
                        fieldLabel: 'Study Contact\'s phone number (numbers only)',
                        name: 'admin_phone_number'
                    }
                ]
            }, {
                margin: '5',
                xtype: 'fieldset',
                title:'Surveys',
                defaults: {
                    anchor: '60%',
                    labelWidth: 400
                },
                defaultType: 'checkbox',
                items: [
                    {
                        fieldLabel: 'Allow free response questions to accept blank answers',
                        name: 'allow_blank_free_response'
                    },{
                        fieldLabel: 'Allow multi choice questions to be answered with no choices',
                        name: 'allow_no_choices'
                    },{
                        fieldLabel: 'Show survey names in app',
                        name: 'show_survey_name'
                    }
                ]
            }, {
                margin: '5',
                xtype: 'fieldset',
                title:'Tracking',
                defaults: {
                    anchor: '60%',
                    labelWidth: 400
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: 'Tracking area center longitude',
                        name: 'location_tracked.0.long'
                    },{
                        fieldLabel: 'Tracking area center latitude',
                        name: 'location_tracked.0.lat'
                    },{
                        fieldLabel: 'Tracking area radius (in kilometers)',
                        name: 'location_tracked.0.radius'
                    },{
                        fieldLabel: 'Start of time tracked (hhmm)',
                        name: 'time_tracked.0.start'
                    },{
                        fieldLabel: 'End of time tracked (hhmm)',
                        name: 'time_tracked.0.end'
                    },{
                        fieldLabel: 'How often (in minutes) should location information be collected',
                        name: 'location_interval'
                    }
                ]
            }, {
                margin: '5',
                xtype: 'fieldset',
                title:'Security',
                defaults: {
                    anchor: '60%',
                    labelWidth: 400
                },
                defaultType: 'checkbox',
                items: [
                    {
                        fieldLabel: 'Use secure (HTTPS) transmission',
                        name: 'https'
                    }
                ]
            }, {
                margin: '5',
                xtype: 'fieldset',
                title:'Technical',
                defaults: {
                    anchor: '60%',
                    labelWidth: 400
                },
                defaultType: 'textfield',
                items: [
                    {
                        fieldLabel: 'Pull interval (in minutes)',
                        name: 'pull_interval'
                    },{
                        fieldLabel: 'Push interval (in minutes)',
                        name: 'push_interval'
                    },{
                        fieldLabel: 'Survey Scheduler interval (in minutes)',
                        name: 'scheduler_interval'
                    },{
                        fieldLabel: 'Survey name / IP (WARNING: DO NOT CHANGE UNLESS YOU KNOW WHAT YOU ARE DOING)',
                        name: 'server'
                    },{
                        fieldLabel: 'Format for recorded audio',
                        name: 'voice_format',
                        xtype: 'combo',
                        anchor: '',
                        valueField: 'value',
                        forceSelection: true,
                        store: Ext.create('Ext.data.ArrayStore', {
                            fields: ['value', 'text'],
                            data: [
                                ['3gp', '3GP'],
                                ['mpeg4', 'MPEG-4']
                            ]
                        }),
                        queryMode: 'local'
                    }
                ]
            }
        ]

    }]
});