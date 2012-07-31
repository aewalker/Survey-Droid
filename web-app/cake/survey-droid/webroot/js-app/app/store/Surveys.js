Ext.define('SD.store.Surveys', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.Survey',
    model: 'SD.model.Survey',
    autoLoad: true,
    autoSync: true
});