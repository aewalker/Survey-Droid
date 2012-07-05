Ext.define('SD.view.Header', {
    extend: 'Ext.Component',
    dock: 'top',
    baseCls: 'app-header',
    initComponent: function() {
        Ext.applyIf(this, { 
            html: 'Survey Droid Web App <span style="float:right;">' +
                '<a href="/users/logout" style="font-size: smaller; color: yellow;">Logout</a></span>'
        });
        this.callParent(arguments);
    }
});