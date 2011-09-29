Ext.define('SD.model.Question', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',          type: 'int',  useNull: true},
        {name: 'survey_id',   type: 'int',  useNull: true},
        {name: 'q_type',      type: 'int'},
        {name: 'q_text',      type: 'string'},
        {name: 'q_img_low',   type: 'string'},
        {name: 'q_img_high',  type: 'string'},
        {name: 'q_text_low',  type: 'string'},
        {name: 'q_text_high', type: 'string'}
    ],
    validations: [
        {type: 'presence',  field: 'q_text'},
        {type: 'presence',  field: 'survey_id'}
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
            storeConfig: {
                autoLoad: true,
                autoSync: true
            }
        }, {
            type: 'hasMany',
            model: 'SD.model.Branch',
            associationKey: 'branches',
            foreignKey: 'question_id',
            name: 'branches',
            storeConfig: {
                autoLoad: true,
                autoSync: true
            }
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/questions'
    }
});