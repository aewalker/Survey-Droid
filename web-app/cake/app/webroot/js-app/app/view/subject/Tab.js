Ext.define("SD.view.subject.Tab", {
    alias: 'widget.subjectsTab',
    extend: "Ext.grid.Panel",
    requires: ['Ext.layout.container.Border'],
    title: 'Subjects',
    store: 'Subjects',
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        items: [{
            action: 'add',
            text: 'Add Subject',
            iconCls: 'icon-add'
        }, '-', {
            action: 'delete',
            text: 'Delete Subject',
            iconCls: 'icon-delete',
            disabled: true
        }]
    }],
    columns: [
        {
            text: 'Subject Id',
            dataIndex: 'id'
        }, {
            text: 'First Name',
            dataIndex: 'first_name',
            editor: {}
        }, {
            text: 'Last Name',
            dataIndex: 'last_name',
            editor: {}
        }, {
            text: 'Phone Number',
            dataIndex: 'phone_num',
            editor: {}
        }, {
            text: 'Device Id',
            dataIndex: 'device_id',
            editor: {}
        }, {
            text: 'Inactive?',
            dataIndex: 'is_inactive',
            xtype: 'booleancolumn',
            editor: {}
        }, {
            text: 'Mutable Subject Id',
            dataIndex: 'mutable_id',
            editor: {}
        }
    ],
    plugins: [
        'rowediting'
    ]
});