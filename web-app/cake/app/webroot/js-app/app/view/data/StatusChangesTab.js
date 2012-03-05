Ext.define("SD.view.data.StatusChangesTab", {
    alias: 'widget.dataStatusChangesTab',
    extend: "Ext.panel.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Status Changes',
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
        }]
    }, {
        region: 'center',
        flex: 4,
        layout: 'fit',
        xtype: 'grid',
        tbar: [{
            text: 'Export'
        }],
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
                    getFeature: function(type) {// enum {gps, calllog, textlog, surveys} - what is being enabled / disabled
                        switch (type) {
                            case 0:
                                return 'GPS';
                            case 1:
                                return 'Call Log';
                            case 2:
                                return 'Text Log';
                            case 3:
                                return 'Surveys';
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
    }]
});
