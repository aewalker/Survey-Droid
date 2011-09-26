Ext.define("SD.view.subject.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.subjectsGrid',
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
        }
    ],
    plugins: [
        'rowediting'
    ]
});