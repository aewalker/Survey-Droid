Ext.define('SD.store.Subjects', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.Subject',
    model: 'SD.model.Subject',
    autoLoad: true,
    autoSync: true
});