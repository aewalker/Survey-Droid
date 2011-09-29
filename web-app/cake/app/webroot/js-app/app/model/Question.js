Ext.define('SD.model.Question', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',          type: 'int'},
        {name: 'survey_id',   type: 'int'},
        {name: 'q_type',      type: 'int'},
        {name: 'q_text',      type: 'string'},
        {name: 'q_img_low',   type: 'string'},
        {name: 'q_img_high',  type: 'string'},
        {name: 'q_text_low',  type: 'string'},
        {name: 'q_text_high', type: 'string'}
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'SD.model.Survey',
            associationKey: 'survey',
            foreignKey: 'question_id',
            getterName: 'getSurvey',
            setterName: 'setSurvey'
        }, {
            type: 'hasMany',
            model: 'SD.model.Choice',
            associationKey: 'choices',
            foreignKey: 'question_id',
            name: 'choices',
            autoLoad: true
        }, {
            type: 'hasMany',
            model: 'SD.model.Branch',
            associationKey: 'branches',
            foreignKey: 'question_id',
            name: 'branches',
            autoLoad: true
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/questions'
    }
});