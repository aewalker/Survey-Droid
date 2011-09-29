Ext.define('SD.model.Survey', {
    extend: 'Ext.data.Model',
    requires: ['SD.model.Question'],
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
    validations: [
        {
            type: 'presence',
            field: 'name'
        }
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'SD.model.Question',
            associationKey: 'question',
            foreignKey: 'question_id',
            getterName: 'getFirstQuestion',
            setterName: 'setFirstQuestion'
        }, {
            type: 'hasMany',
            model: 'SD.model.Question',
            associationKey: 'questions',
            foreignKey: 'survey_id',
            name: 'questions',
            autoLoad: true
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/surveys'
    }
});
