Ext.define("Soc.view.subject.Grid", {
    extend: "Ext.grid.Panel",
    alias: 'widget.subjectsGrid',
    title: 'Subjects',
    store: 'Subjects',
    forceFit: true,
    dockedItems: [{
        xtype: 'toolbar',
        items: [{
            text: 'Add',
            iconCls: 'icon-add',
            handler: function(){
//                // empty record
//                store.insert(0, new Person());
//                rowEditing.startEdit(0, 0);
            }
        }, '-', {
            itemId: 'delete',
            text: 'Delete',
            iconCls: 'icon-delete',
            disabled: true,
            handler: function(){
//                var selection = grid.getView().getSelectionModel().getSelection()[0];
//                if (selection) {
//                    store.remove(selection);
//                }
            }
        }]
    }],
    columns: [
        {
            text: 'Id',
            dataIndex: 'id'
        }, {
            text: 'First Name',
            dataIndex: 'first_name',
            editor: { allowBlank: false }
        }, {
            text: 'Last Name',
            dataIndex: 'last_name',
            editor: { allowBlank: false }
        }, {
            text: 'Phone Number',
            dataIndex: 'phone_num',
            editor: {}
        }, {
            text: 'Device Id',
            dataIndex: 'device_id',
            editor: { allowBlank: false }
        }
    ],
    plugins: [
        Ext.create('Ext.grid.plugin.RowEditing', {
            clicksToMoveEditor: 1,
            autoCancel: false
        })
    ]
});