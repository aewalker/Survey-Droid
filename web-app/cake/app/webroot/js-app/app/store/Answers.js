Ext.define('SD.store.Answers', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.Answer',
    model: 'SD.model.Answer',
    autoLoad: true,
    autoSync: true
});