/*
 A condition for a BRANCH evaluates to true only if the answer to QUESTION  is CHOICE
 */
Ext.define('SD.model.Condition', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'branch_id',    type: 'int'},
        {name: 'question_id',    type: 'int'},
        {name: 'choice_id',    type: 'int'},
        {name: 'type',    type: 'int'}
    ],
    belongsTo: ['Branch', 'Question', 'Choice'],
    proxy: {
        type: 'rest',
        url : '/rest/conditions',
        reader: {
            type: 'json',
            record: 'Condition'
        }
    }
});