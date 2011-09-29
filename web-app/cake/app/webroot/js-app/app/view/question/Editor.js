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
            itemId: 'questionDetails',
            xtype: 'questionDetails',
            flex: 1
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