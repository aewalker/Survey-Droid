Ext.define("SD.view.data.LocationsTab", {
    alias: 'widget.dataLocationsTab',
    extend: "Ext.panel.Panel",
    requires: ['Ext.selection.CheckboxModel', 'Ext.grid.column.Date', 'Ext.grid.column.Template'],
    title: 'Location (Only the last 300 are displayed)',
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
        store: 'Locations',
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
    }]
});
