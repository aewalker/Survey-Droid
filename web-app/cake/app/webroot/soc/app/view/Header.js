Ext.define('Books.view.Header', {
    extend: 'Ext.Component',
    dock: 'top',
    baseCls: 'app-header',
    initComponent: function() {
        Ext.applyIf(this, {
            html: 'Loading nested data example'
        });
        this.callParent(arguments);
    }
});