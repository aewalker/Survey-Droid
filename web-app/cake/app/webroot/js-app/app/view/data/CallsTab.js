Ext.define("SD.view.data.CallsTab", {
    alias: 'widget.dataCallsTab',
    extend: "Ext.panel.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Call Log(Only last 300 are displayed)',
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
        store: 'Calls',
        tbar: [{
            text: 'Export'
        }],
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
                            case 1:
                                return 'Incoming Call';
                            case 2:
                                return 'Outgoing Call';
                            case 3:
                                return 'Missed Call';
                            case 4:
                                return 'Incoming Text';
                            case 5:
                                return 'Outgoing Text';
                        }
                    }
                })
            }, {
                text: 'Call Duration (in seconds)',
                dataIndex: 'duration',
                width: 150
            }
        ]
    }]
});
