Ext.define('Soc.view.Header', {
    extend: 'Ext.Component',
    dock: 'top',
    baseCls: 'app-header',
    initComponent: function() {
        Ext.applyIf(this, {
            html: 'Soc App'
        });
        this.callParent(arguments);
    }
});