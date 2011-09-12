Ext.define("Soc.view.user.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.usersGrid',
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
            editor: {}
        }
    ],
    plugins: [
        'rowediting'
    ]
});