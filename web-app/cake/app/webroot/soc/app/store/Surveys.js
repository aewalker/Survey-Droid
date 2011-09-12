Ext.define('Soc.store.Surveys', {
    extend: 'Ext.data.Store',
    requires: 'Soc.model.Survey',
    model: 'Soc.model.Survey',
    autoLoad: true
});