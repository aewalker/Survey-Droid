Ext.define("SD.view.question.Details", {
    extend: "Ext.form.Panel",
    alias: 'widget.questionDetails',
    requires: ['Ext.form.FieldContainer', 'Ext.form.FieldSet', 'Ext.form.field.Radio'],
    layout: {
        type: 'vbox',
        align: 'stretch'
    },
    buttonAlign: 'left',
    buttons: [{
        text: 'Save',
        action: 'save-question'
    }, {
        text: 'Delete',
        action: 'delete-question'
    }],
    items: [
        {
            flex: 1,
            margin: '5',
            xtype: 'fieldset',
            title:'Question Details',
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
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
                }, {
                    xtype: 'panel',
                    itemId: 'typePanel',
                    flex: 1,
                    margin: '0 0 8 0',
                    border: false,
                    layout: 'card',
                    items: [
                        {
                            itemId: 'empty',
                            xtype: 'panel',
                            border: false
                        }, {
                            itemId: 'scaletext',
                            xtype: 'fieldcontainer',
                            defaultType: 'textfield',
                            defaults: {
                                labelAlign: 'top',
                                margin: '0 10'
                            },
                            layout: {
                                type: 'hbox',
                                pack: 'center'
                            },
                            items: [
                                {
                                    fieldLabel: 'Low-end Text',
                                    name: 'q_text_low'
                                }, {
                                    fieldLabel: 'High-end Text',
                                    name: 'q_text_high'
                                }
                            ]
                        }, {
                            itemId: 'scaleimage',
                            xtype: 'fieldcontainer',
                            defaultType: 'textfield',
                            defaults: {
                                labelAlign: 'top',
                                margin: '0 10'
                            },
                            layout: {
                                type: 'vbox',
                                align: 'center'
                            },
                            items: [
                                {
                                    xtype: 'panel',
                                    html: 'Please paste the <a href="http://www.dopiaza.org/tools/datauri/" target="_blank">base64 encoded</a> image into the text field',
                                    border: false
                                }, {
                                    fieldLabel: 'Low-end Image',
                                    name: 'q_img_low'
                                }, {
                                    fieldLabel: 'High-end Image',
                                    name: 'q_img_high'
                                }
                            ]
                        }, {
                            itemId: 'choicesList',
                            xtype: 'grid',
                            flex: 1,
                            title: 'Choices',
                            store: Ext.create('Ext.data.ArrayStore'),
                            dockedItems: [{
                                xtype: 'toolbar',
                                items: [{
                                    action: 'add',
                                    text: 'Add Choice',
                                    iconCls: 'icon-add'
                                }, '-', {
                                    action: 'delete',
                                    text: 'Delete Choice',
                                    iconCls: 'icon-delete',
                                    disabled: true
                                }]
                            }],
                            columns: [
                                {
                                    text: 'Id',
                                    dataIndex: 'id',
                                    width: 35
                                }, {
                                    xtype: 'templatecolumn',
                                    text: 'Choice Type',
                                    tpl: new Ext.XTemplate(
                                        '{[ this.types[values.choice_type] ]}',
                                        { types: { 0: 'Text', 1: 'Image' } }
                                    ),
                                    dataIndex: 'choice_type',
                                    editor: {
                                        xtype: 'combo',
                                        valueField: 'value',
                                        forceSelection: true,
                                        store: Ext.create('Ext.data.ArrayStore', {
                                            fields: ['value', 'text'],
                                            data: [
                                                [0, 'Text'],
                                                [1, 'Image']
                                            ]
                                        }),
                                        queryMode: 'local'
                                    },
                                    flex: 1
                                }, {
                                    text: 'Choice Text',
                                    dataIndex: 'choice_text',
                                    editor: {},
                                    flex: 1
                                }, {
                                    text: 'Choice Image',
                                    dataIndex: 'choice_img',
                                    flex: 1
                                }
                            ],
                            plugins: [
                                'cellediting'
                            ]
                        }
                    ]
                }
            ]
        }
    ]
});