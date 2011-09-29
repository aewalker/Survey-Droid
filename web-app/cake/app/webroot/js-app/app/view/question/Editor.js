Ext.define("SD.view.question.Editor", {
    extend: "Ext.panel.Panel",
    requires: ['SD.view.question.Details'],
    alias: 'widget.questionEditor',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    items: [
        {
            xtype: 'panel',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    itemId: 'questionDetails',
                    xtype: 'questionDetails',
                    flex: 1
                }, {
                    itemId: 'choicesList',
                    xtype: 'grid',
                    flex: 1,
                    title: 'Choices',
                    store: Ext.create('Ext.data.Store', {model: 'SD.model.Choice'} ),
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
                            flex: 1
                        }, {
                            text: 'Choice Text',
                            dataIndex: 'choice_text',
                            flex: 1
                        }, {
                            text: 'Choice Image',
                            dataIndex: 'choice_img',
                            flex: 1
                        }
                    ]
                }
            ]
        }, {
            xtype: 'panel',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            items: [
                {
                    itemId: 'branchesList',
                    xtype: 'grid',
                    flex: 1,
                    title: 'Branches',
                    store: Ext.create('Ext.data.Store', {model: 'SD.model.Branch'} ),
                    columns:[
                        {
                            text: 'Id',
                            dataIndex: 'id',
                            width: 35
                        }, {
                            text: 'Next Question',
                            dataIndex: 'next_q',
                            flex: 1
                        }
                    ]
                }, {
                    itemId: 'conditionsList',
                    xtype: 'panel',
                    flex: 1,
                    title: 'Conditions'
                }
            ]
        }
    ]
});