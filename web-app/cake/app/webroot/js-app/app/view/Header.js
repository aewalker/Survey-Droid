Ext.define('SD.view.Header', {
    extend: 'Ext.Component',
    dock: 'top',
    baseCls: 'app-header',
    initComponent: function() {
        Ext.applyIf(this, {
            html: 'Survey Droid Web App'
        });
        this.callParent(arguments);
    }
});