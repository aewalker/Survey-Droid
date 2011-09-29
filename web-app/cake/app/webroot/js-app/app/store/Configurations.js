Ext.define('SD.store.Configurations', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.Configuration',
    model: 'SD.model.Configuration',
    //autoLoad: true,
    autoSync: true
});