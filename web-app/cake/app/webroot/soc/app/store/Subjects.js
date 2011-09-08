Ext.define('Soc.store.Subjects', {
    extend: 'Ext.data.Store',
    requires: 'Soc.model.Subject',
    model: 'Soc.model.Subject',
    autoLoad: true,
    autoSync: true
});