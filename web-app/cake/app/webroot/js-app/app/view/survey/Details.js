Ext.define("SD.view.survey.Details", {
    extend: "Ext.form.Panel",
    alias: 'widget.surveyDetails',
    autoScroll: true,
    buttonAlign: 'left',
    buttons: [{
        text: 'Save',
        action: 'save'
    }, {
        text: 'Delete',
        action: 'delete'
    }],
    items: [
        {
            margin: '5',
            xtype: 'fieldset',
            title:'Survey Details',
            defaults: {
                anchor: '100%',
                labelWidth: 100
            },
            defaultType: 'textfield',
            items: [
                {
                    xtype: 'displayfield',
                    fieldLabel: 'Survey Id',
                    name: 'id'
                },{
                    fieldLabel: 'Name',
                    name: 'name',
                    allowBlank: false
                },{
                    fieldLabel: 'First Question',
                    name: 'question_id'
                },{
                    xtype: 'textarea',
                    fieldLabel: 'Subject Specific Variables',
                    name: 'subject_variables'
                }
            ]
        }, {
            margin: '5',
            xtype: 'fieldset',
            title:'Survey Triggers',
            defaults: {
                labelWidth: 100
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: 'Monday',
                    name: 'mo'
                },{
                    fieldLabel: 'Tuesday',
                    name: 'tu'
                },{
                    fieldLabel: 'Wednesday',
                    name: 'we'
                },{
                    fieldLabel: 'Thursday',
                    name: 'th'
                },{
                    fieldLabel: 'Friday',
                    name: 'fr'
                },{
                    fieldLabel: 'Saturday',
                    name: 'sa'
                },{
                    fieldLabel: 'Sunday',
                    name: 'su'
                },{
                    xtype: 'checkbox',
                    fieldLabel: 'Can be initialized by subject',
                    name: 'subject_init'
                }
            ]
        }
    ]
});