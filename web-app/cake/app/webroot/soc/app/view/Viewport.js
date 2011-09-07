Ext.define('Soc.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: ['Soc.store.Subjects', 'Soc.view.Header', 'Soc.view.Tabs'],
    layout: 'fit',
    items: [
        {
            xtype: "panel",
            dockedItems: [
                Ext.create('Soc.view.Header')
            ],
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
