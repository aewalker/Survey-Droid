Ext.define('SD.view.survey.QuestionsPanel', {
    extend: 'Ext.panel.Panel',
    requires: ['SD.view.question.Editor', 'Ext.form.field.ComboBox', 'Ext.toolbar.Toolbar'],
    alias: 'widget.questionsPanel',
    layout: {
        type: 'hbox',
        align: 'stretch'
    },
    items: [
        {
            itemId: 'questionsList',
            xtype: 'grid',
            border: false,
            title: 'Questions',
            flex: 1,
            dockedItems: [{
                xtype: 'toolbar',
                items: [{
                    action: 'add',
                    text: 'Add Question',
                    iconCls: 'icon-add'
                }]
            }],
            store: Ext.create('Ext.data.ArrayStore'),
            columns: [
                {
                    text: 'Id',
                    dataIndex: 'id',
                    width: 35
                }, {
                    text: 'Question',
                    dataIndex: 'q_text',
                    flex: 1,
                    editor: {}
                }
            ],
            plugins: [
                'cellediting'
            ]
        }, {
            xtype: 'questionEditor',
            border: false,
            flex: 4
        }
    ]
});