Ext.define('Books.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: ['Books.store.Books', 'Books.view.book.Sidebar', 'Books.view.Header'],
    layout: 'fit',
    items: {
        xtype: 'panel',
        border: false,
        id    : 'viewport',
        layout: {
            type: 'vbox',
            align: 'stretch'
        },
        dockedItems: [
            Ext.create('Books.view.Header'),
            Ext.create('Books.view.book.Sidebar')
        ],
        items: [
            Ext.create('Books.view.book.View'),
            Ext.create('Books.view.review.List')
//            {
//                xtype: 'grid',
//                title: 'Users',
//                store: 'Books',
//                columns: [
//                    {
//                        text: 'Id',
//                        dataIndex: 'id'
//                    }, {
//                        text: 'name',
//                        dataIndex: 'name'
//                    }, {
//                        text: 'detail',
//                        dataIndex: 'detail',
//                        flex: 1
//                    }, {
//                        text: 'price',
//                        dataIndex: 'price'
//                    }, {
//                        text: 'image',
//                        dataIndex: 'image',
//                        flex: 1
//                    }
//                ]
//            }
        ]
    },
    initComponent: function() {
        this.doLayout();
        this.callParent(arguments);
    }
});
