Ext.define("Soc.view.subject.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.subjectsGrid',
    title: 'Subjects',
    store: 'Subjects',
    columns: [
        {
            text: 'Id',
            dataIndex: 'id'
        }, {
            text: 'First Name',
            dataIndex: 'first_name'
        }, {
            text: 'Last Name',
            dataIndex: 'last_name'
        }, {
            text: 'Phone Number',
            dataIndex: 'phone_num'
        }, {
            text: 'Device Id',
            dataIndex: 'device_id'
        }
    ]
});