Ext.define("SD.view.data.SurveysTakenTab", {
    alias: 'widget.dataSurveysTakenTab',
    extend: "Ext.panel.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Surveys Taken (Only last 300 are displayed)',
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
        items: [{
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
        }]
    }, {
        region: 'center',
        flex: 4,
        layout: 'fit',
        xtype: 'grid',
        tbar: [{
            text: 'Export'
        }],
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
                text: 'Themometer %',
                xtype: 'templatecolumn',
                width: 100,
                dataIndex: 'rate',
                tpl: new Ext.XTemplate(
                    '{[this.getPercentage(values.rate)]}', {
                    getPercentage: function(rate) {
                        if (rate != null)
                            return rate + '%';
                        return '';
                    }
                })
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
                            case 12: return 'Call Initiated Survey Finished';
                            case 13: return 'Call Initiated Survey Unfinished';
                            case 14: return 'Call Initiated Survey Dismissed';
                            case 15: return 'Call Initiated Survey Ignored';
                            case 16: return 'Location Based Survey Finished';
                            case 17: return 'Location Based Survey Unfinished';
                            case 18: return 'Location Based Survey Dismissed';
                            case 19: return 'Location Based Survey Ignored';
                        }
                    }
                })
            }
        ]
    }]
});
