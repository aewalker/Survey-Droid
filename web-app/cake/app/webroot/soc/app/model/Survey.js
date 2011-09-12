Ext.define('Soc.model.Survey', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',                type: 'int'},
        {name: 'name',              type: 'string'},
        {name: 'question_id',       type: 'int'},
        {name: 'mo',                type: 'string'},
        {name: 'tu',                type: 'string'},
        {name: 'we',                type: 'string'},
        {name: 'th',                type: 'string'},
        {name: 'fr',                type: 'string'},
        {name: 'sa',                type: 'string'},
        {name: 'su',                type: 'string'},
        {name: 'subject_init',      type: 'boolean'},
        {name: 'subject_variables', type: 'string'},
        {name: 'created',           type: 'date',   dateFormat: Ext.Date.patterns.ISO8601Long}
    ],
    belongsTo: 'Soc.model.Question', // Each survey has a starting questio
    proxy: {
        type: 'rest',
        url : '/rest/surveys',
        reader: {
            type: 'json',
            record: 'Survey'
        }
    }
});
