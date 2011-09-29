Ext.define("SD.view.question.Details", {
    extend: "Ext.form.Panel",
    alias: 'widget.questionDetails',
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
            title:'Question Details',
            defaults: {
                anchor: '100%',
                labelWidth: 100
            },
            defaultType: 'textfield',
            items: [
                {
                    fieldLabel: 'Question Text',
                    name: 'q_text',
                    allowBlank: false
                }, {
                    xtype: 'fieldcontainer',
                    fieldLabel: 'Question Type',
                    defaultType: 'radiofield',
                    defaults: {
                        flex: 1,
                        name: 'q_type'
                    },
                    items: [
                        {
                            boxLabel: 'Single Choice',
                            inputValue: 0
                        }, {
                            boxLabel: 'Multiple Choice',
                            inputValue: 1
                        }, {
                            boxLabel: 'Scale Text',
                            inputValue: 2
                        }, {
                            boxLabel: 'Scale Image',
                            inputValue: 3
                        }, {
                            boxLabel: 'Free Response',
                            inputValue: 4
                        }
                    ]

                }
            ]
        }
    ]
});