Ext.define("SD.view.data.AnswersTab", {
    alias: 'widget.dataAnswersTab',
    extend: "Ext.panel.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Survey Answers(Only last 300 are displayed)',
    layout: 'border',
    items: [{
        region: 'west',
        xtype: 'panel',
        flex: 1,
        border: false,
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        items: [
            {
                itemId: 'subjectFilter',
                xtype: 'grid',
                title: 'Filter By Subject',
                store: 'Subjects',
                columns: [
                {
                    dataIndex: 'id',
                    width: 50
                }, {
                    dataIndex: 'id',
                    xtype: 'templatecolumn',
                    tpl: '{first_name} {last_name}',
                    flex: 1
                }],
                selModel: Ext.create('Ext.selection.CheckboxModel', {
                    mode: 'SINGLE',
                    allowDeselect: true
                }),
                hideHeaders: true,
                flex: 1
            }, {
                itemId: 'surveyFilter',
                xtype: 'grid',
                title: 'Filter By Surveys',
                store: 'Surveys',
                columns: [{
                    dataIndex: 'id',
                    width: 20
                },{
                    dataIndex: 'name',
                    flex: 1
                }],
                selModel: Ext.create('Ext.selection.CheckboxModel', {
                    mode: 'SINGLE',
                    allowDeselect: true
                }),
                hideHeaders: true,
                flex: 1
            }
        ]
    }, {
        region: 'center',
        xtype: 'panel',
        title: 'Answers List',
        flex: 4,
        tbar: [{
            text: 'Export'
        }],
        layout: 'fit',
        items: [
            {
                xtype: 'grid',
                store: 'Answers',
                columns: [
                    {
                        text: 'Subject Id',
                        dataIndex: 'subject_id',
                        width: 70
                    }, {
                        text: 'Subject',
                        xtype: 'templatecolumn',
                        dataIndex: 'subject_id',
                        tpl: '{subject.first_name} {subject.last_name}',
                        width: 100
                    }, {
                        text: 'Survey Id',
                        dataIndex: 'survey_id',
                        width: 75
                    }, {
                        text: 'Question',
                        xtype: 'templatecolumn',
                        dataIndex: 'question_id',
                        tpl: '{question.q_text}',
                        width: 300
                    },{
                        text: 'Answer',
                        xtype: 'templatecolumn',
                        tpl: new Ext.XTemplate('{[this.getDisplayAnswer(values)]}',{
                            getDisplayAnswer: function(answer) {
                                switch (answer.ans_type) {
                                    case 0:
                                        var choices = [];
                                        for (var i = 0; i < answer.choices.length; i++) {
                                            var choice = answer.choices[i];
                                            choices.push(choice.choice_text);
                                        }
                                        return choices.join(', ');
                                    case 1:
                                        return answer.ans_value;
                                    case 2:
                                        return answer.ans_text;
                                }
                                return 'Undefined Type';
                            }
                        }),
                        flex: 1
                    }, {
                        text: 'Answer Type',
                        xtype: 'templatecolumn',
                        dataIndex: 'ans_type',
                        tpl: new Ext.XTemplate('{[this.getTypeText(values.ans_type)]}',{
                            getTypeText: function(ans_type) {
                                switch (ans_type) {
                                    case 0:
                                        return 'Single/Multiple Choice';
                                    case 1:
                                        return 'Numerical Value';
                                    case 2:
                                        return 'Text';
                                }
                                return 'Undefined Type';
                            }
                        }),
                        width: 150
                    }, {
                        text: 'Time Answered',
                        xtype: 'datecolumn',
                        dataIndex: 'created',
                        format: 'Y-m-d H:i:s',
                        width: 150
                    }
                ]
            }
        ]
    }]
});
