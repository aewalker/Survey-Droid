Ext.define("Soc.view.user.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.usersGrid',
    title: 'Users',
    store: 'Users',
    columns: [
        {
            text: 'Id',
            dataIndex: 'id'
        }, {
            text: 'Username',
            dataIndex: 'username'
        }, {
            text: 'Email',
            dataIndex: 'email'
        }, {
            text: 'First Name',
            dataIndex: 'first_name'
        }, {
            text: 'Last Name',
            dataIndex: 'last_name'
        }, {
            text: 'Admin',
            dataIndex: 'admin'
        }
    ]
});