Ext.define('SD.model.Branch', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'question_id',    type: 'int'},
        {name: 'next_q',    type: 'int'}
    ],
    validations: [
        {type: 'presence',  field: 'question_id'},
        {type: 'presence',  field: 'next_q'}
    ],
    associations: [
        {
            type: 'belongsTo',
            model: 'SD.model.Question',
            associationKey: 'question',
            foreignKey: 'question_id',
            getterName: 'getQuestion',
            setterName: 'setQuestion'
        }, {
            type: 'belongsTo',
            model: 'SD.model.Question',
            associationKey: 'next_question',
            foreignKey: 'next_q',
            getterName: 'getNextQuestion',
            setterName: 'setNextQuestion'
        }, {
            type: 'hasMany',
            model: 'SD.model.Condition',
            associationKey: 'conditions',
            foreignKey: 'branch_id',
            name: 'conditions',
            storeConfig: {
                storeId: 'BranchesByQuestion',
                autoLoad: true,
                autoSync: true
            }
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/branches'
    }
});