Ext.define("SD.view.data.Tab", {
    alias: 'widget.dataTab',
    extend: "Ext.tab.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Data',
    items: [
        {
            itemId: 'answersTab',
            title: 'Survey Answers',
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
                            width: 20
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
                                                return answer.answer_text;
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
        }, {
            itemId: 'locationsTab',
            title: 'Location',
            xtype: 'grid',
            store: 'Locations',
            columns: [
                {
                    text: 'Time',
                    dataIndex: 'created',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 150
                }, {
                    text: 'Subject Id',
                    dataIndex: 'subject_id',
                    width: 80
                }, {
                    text: 'Longitude',
                    dataIndex: 'longitude',
                    width: 100
                }, {
                    text: 'Latitude',
                    dataIndex: 'latitude',
                    width: 100
                }, {
                    text: 'Accuracy Radius (meters)',
                    dataIndex: 'accuracy',
                    width: 150
                }
            ]
        }, {
            itemId: 'callsTab',
            title: 'Call Log',
            xtype: 'grid',
            store: 'Calls',
            columns: [
                {
                    text: 'Time',
                    dataIndex: 'created',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 150
                }, {
                    text: 'Subject Id',
                    dataIndex: 'subject_id',
                    width: 70
                }, {
                    xtype: 'templatecolumn',
                    text: 'Subject',
                    dataIndex: 'subject_id',
                    tpl: '{subject.first_name} {subject.last_name}',
                    width: 150
                }, {
                    text: 'Contact Id',
                    dataIndex: 'contact_id',
                    width: 230
                }, {
                    text: 'Call Type',
                    xtype: 'templatecolumn',
                    dataIndex: 'type',
                    tpl: new Ext.XTemplate(
                        '{[this.getCallType(values.type)]}', {
                        getCallType: function(type) {
                            switch (type) {
                                case 0:
                                    return 'Outgoing Call';
                                case 1:
                                    return 'Incoming Call';
                                case 2:
                                    return 'Outgoing Text';
                                case 3:
                                    return 'Incoming Text';
                                case 4:
                                    return 'Missed Call';
                            }
                        }
                    })
                }, {
                    text: 'Call Duration (in seconds)',
                    dataIndex: 'duration',
                    width: 150
                }
            ]
        }, {
            itemId: 'statuschangesTab',
            title: 'Status Changes',
            xtype: 'grid',
            store: 'StatusChanges',
            columns: [
                {
                    text: 'Time',
                    dataIndex: 'created',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 150
                }, {
                    text: 'Subject Id',
                    dataIndex: 'subject_id',
                    width: 80
                }, {
                    text: 'Feature',
                    xtype: 'templatecolumn',
                    dataIndex: 'feature',
                    tpl: new Ext.XTemplate(
                        '{[this.getFeature(values.feature)]}', {
                        getFeature: function(type) {// enum {gps, calllog, textlog, app} - what is being enabled / disabled
                            switch (type) {
                                case 0:
                                    return 'GPS';
                                case 1:
                                    return 'Call Log';
                                case 2:
                                    return 'Text Log';
                                case 3:
                                    return 'Application';
                            }
                        }
                    })
                }, {
                    text: 'Action',
                    xtype: 'templatecolumn',
                    dataIndex: 'status',
                    tpl: new Ext.XTemplate(
                        '{[this.getAction(values.status)]}', { // 1 for enabling, or turning on, 0 for turning off or disabling a feature
                        getAction: function(type) {
                            switch (type) {
                                case 0:
                                    return 'Disabling';
                                case 1:
                                    return 'Enabling';
                            }
                        }
                    })
                }
            ]
        },
        {
            itemId: 'surveystakenTab',
            title: 'Surveys Taken',
            xtype: 'grid',
            store: 'SurveysTaken',
            columns: [
                {
                    text: 'Time',
                    dataIndex: 'created',
                    xtype: 'datecolumn',
                    format: 'Y-m-d H:i:s',
                    width: 150
                }, {
                    text: 'Subject Id',
                    dataIndex: 'subject_id',
                    width: 80
                }, {
                    text: 'Survey Id',
                    dataIndex: 'survey_id',
                    width: 80
                }, {
                    text: 'Status',
                    xtype: 'templatecolumn',
                    width: 250,
                    dataIndex: 'status',
                    tpl: new Ext.XTemplate(
                        '{[this.getStatus(values.status)]}', {
                        getStatus: function(type) {
                            switch (type) {
                                case 0: return 'Surveys Disabled Locally';
                                case 1: return 'Surveys Disabled by Server';
                                case 2: return 'User Initiated Survey Finished';
                                case 3: return 'User Initiated Survey Unfinished';
                                case 4: return 'Scheduled Survey Finished';
                                case 5: return 'Scheduled Survey Unfinished';
                                case 6: return 'Scheduled Survey Dismissed';
                                case 7: return 'Scheduled Survey Ignored';
                                case 8: return 'Random Survey Finished';
                                case 9: return 'Random Survey Unfinished';
                                case 10: return 'Random Survey Dismissed';
                                case 11: return 'Random Survey Ignored';
                            }
                        }
                    })
                }
            ]
        }, {
            itemId: 'photosTab',
            title: 'Photos',
            xtype: 'panel',
            layout: {
                type: 'hbox',
                align: 'stretch'
            },
            items: [
                {
                    itemId: 'photosList',
                    xtype: 'grid',
                    flex: 2,
                    store: 'Extras',
                    columns: [
                        {
                            text: 'Time',
                            dataIndex: 'created',
                            xtype: 'datecolumn',
                            format: 'Y-m-d H:i:s',
                            width: 150
                        }, {
                            text: 'Subject Id',
                            dataIndex: 'subject_id'
                        }, {
                            text: 'Type',
                            xtype: 'templatecolumn',
                            width: 100,
                            dataIndex: 'type',
                            tpl: new Ext.XTemplate(
                                '{[this.getType(values.type)]}', {
                                getType: function(type) {
                                    switch (type) {
                                        case 0: return 'Photos';
                                    }
                                }
                            })
                        }, {
                            text: 'Thumbnail',
                            xtype: 'templatecolumn',
                            flex: 1,
                            tpl: '<img class="thumbnail" src="data:image/jpeg;base64,{data}" />'
                        }
                    ]
                }, {
                    itemId: 'fullImage',
                    xtype: 'panel',
                    flex: 3,
                    title: 'Full size image',
                    html: '<img src="" />',
                    autoScroll: true,
                    bind: function(record) {
                        this.getEl().child('div.x-panel-body').update(
                            '<img class="fullsize" src="data:image/jpeg;base64,'+record.data.data+'" />'
                        );
                        this.doLayout();
                    }
                }
            ]
        }
    ]
});