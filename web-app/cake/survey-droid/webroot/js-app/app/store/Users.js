Ext.define('SD.store.Users', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.User',
    model: 'SD.model.User',
    autoLoad: true,
    autoSync: true
});