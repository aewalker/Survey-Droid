Ext.define('SD.store.QuestionsBySurvey', {
    extend: 'Ext.data.Store',
    requires: 'SD.model.Question',
    model: 'SD.model.Question',
    autoSync: true
    
});