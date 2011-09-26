Ext.define('SD.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: ['SD.store.Subjects', 'SD.view.Header', 'SD.view.Tabs'],
    layout: 'fit',
    items: [
        {
            xtype: "panel",
            layout: 'fit',
            dockedItems: [
                Ext.create('SD.view.Header')
            ],
            border: false,
            items: [
                { xtype: 'mainTabs' }
            ]
        }
    ],
    initComponent: function() {
        console.log("viewport initing");
        this.doLayout();
        this.callParent(arguments);
    }
});
