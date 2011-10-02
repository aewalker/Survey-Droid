Ext.define('SD.view.Viewport', {
    extend: 'Ext.container.Viewport',
    requires: ['SD.store.Subjects', 'SD.view.Header', 'SD.view.Tabs', 'Ext.grid.plugin.RowEditing'],
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
    ]
});
