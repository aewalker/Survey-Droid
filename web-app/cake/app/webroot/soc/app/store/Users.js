Ext.define('Soc.store.Users', {
    extend: 'Ext.data.Store',
    requires: 'Soc.model.User',
    model: 'Soc.model.User',
    autoLoad: true
});