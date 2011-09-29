Ext.define('SD.model.Branch', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'question_id',    type: 'int'},
        {name: 'next_q',    type: 'int'}
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
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/branches'
    }
});