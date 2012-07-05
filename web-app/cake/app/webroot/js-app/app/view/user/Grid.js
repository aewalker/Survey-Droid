Ext.define("SD.view.user.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.usersGrid',
    requires: 'Ext.grid.column.Boolean',
    title: 'Users',
    store: 'Users',
    forceFit: true,
    columns: [
        {
            text: 'Id',
            dataIndex: 'id'
        }, {
            text: 'Username',
            dataIndex: 'username',
            editor: {}
        }, {
            text: 'Email',
            dataIndex: 'email',
            editor: {}
        }, {
            text: 'First Name',
            dataIndex: 'first_name',
            editor: {}
        }, {
            text: 'Last Name',
            dataIndex: 'last_name',
            editor: {}
        }, {
            text: 'Admin',
            dataIndex: 'admin',
            xtype: 'booleancolumn',
            trueText: 'Yes',
            falseText: 'No',             
            editor: {}
        }
    ],
    plugins: [
        'rowediting'
    ]
});