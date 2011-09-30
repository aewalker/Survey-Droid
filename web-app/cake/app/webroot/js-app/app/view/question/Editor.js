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
//            border: ',
            flex: 1
        }, {
            xtype: 'panel',
            flex: 1,
            layout: {
                type: 'vbox',
                align: 'stretch'
            },
            border: false,
            items: [
                {
                    itemId: 'branchesList',
                    xtype: 'grid',
                    flex: 1,
                    border: false,
                    title: 'Branches',
                    store: Ext.create('Ext.data.Store', {model: 'SD.model.Branch'} ),
                    dockedItems: [{
                        xtype: 'toolbar',
                        items: [{
                            action: 'add',
                            text: 'Add Branch',
                            iconCls: 'icon-add'
                        }, '-', {
                            action: 'delete',
                            text: 'Delete Branch',
                            iconCls: 'icon-delete',
                            disabled: true
                        }]
                    }],
                    columns:[
                        {
                            text: 'Next Question Id',
                            dataIndex: 'next_q',
                            editor: {}
                        }, {
                            text: 'Next Question',
                            flex: 1,
                            xtype: 'templatecolumn',
                            tpl: new Ext.XTemplate(
                                '{[this.nextQuestionText(values.next_q)]}', {
                                nextQuestionText: function(next_q) {
                                    var question = Ext.getStore('QuestionsBySurvey').getById(next_q);
                                    if (question)
                                        return question.data.q_text;
                                    return 'No question by this id is found';
                                }
                            })
                        }
                    ],
                    plugins: [
                        'cellediting'
                    ]
                }, {
                    itemId: 'conditionsList',
                    xtype: 'grid',
                    border: false,
                    flex: 1,
                    title: 'Conditions',
                    store: Ext.create('Ext.data.Store', {model: 'SD.model.Condition'} ),
                    dockedItems: [{
                        xtype: 'toolbar',
                        items: [{
                            action: 'add',
                            text: 'Add Condition',
                            iconCls: 'icon-add'
                        }, '-', {
                            action: 'delete',
                            text: 'Delete Condition',
                            iconCls: 'icon-delete'
                        }]
                    }],
                    columns: [
                        {
                            xtype: 'templatecolumn',
                            tpl: 'Answer to Question',
                            flex: 1
                        }, {
                            text: 'Question',
                            dataIndex: 'question_id',
                            editor: {},
                            flex: 1
                        }, {
                            xtype: 'templatecolumn',
                            text: 'Condition Type',
                            tpl: new Ext.XTemplate(
                                '{[ this.types[values.type] ]}',
                                { types: { 0: 'just was', 1: 'ever was', 2: 'never has been' } }
                            ),
                            dataIndex: 'type',
                            editor: {
                                xtype: 'combo',
                                valueField: 'value',
                                forceSelection: true,
                                store: Ext.create('Ext.data.ArrayStore', {
                                    fields: ['value', 'text'],
                                    data: [
                                        [0, 'just was'],
                                        [1, 'ever was'],
                                        [2, 'never has been']
                                    ]
                                }),
                                queryMode: 'local'
                            },
                            flex: 1
                        }, {
                            text: 'Choice',
                            dataIndex: 'choice_id',
                            editor: {},
                            flex: 1
                        }
                    ],
                    plugins: [
                        'rowediting'
                    ]
                }
            ]
        }
    ]
});