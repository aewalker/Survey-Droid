Ext.define('Soc.model.Choice', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'choice_type',    type: 'int'},
        {name: 'choice_text',    type: 'string'},
        {name: 'choice_img',    type: 'string'},
        {name: 'question_id',    type: 'int'}
    ],
    belongsTo: 'Question', // each choice belongs to a question
    proxy: {
        type: 'rest',
        url : '/rest/choices',
        reader: {
            type: 'json',
            record: 'Choice'
        }
    }
});