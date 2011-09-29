Ext.define('SD.model.Choice', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'choice_type',    type: 'int'},
        {name: 'choice_text',    type: 'string'},
        {name: 'choice_img',    type: 'string'},
        {name: 'question_id',    type: 'int'}
    ],
    associations: [{
        type: 'belongsTo',
        model: 'SD.model.Question',
        associationKey: 'question',
        foreignKey: 'question_id',
        getterName: 'getQuestion',
        setterName: 'setQuestion'
    }],
    proxy: {
        type: 'rest',
        url : '/rest/choices'
    }
});