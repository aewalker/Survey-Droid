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
    validations: [
        {type: 'presence',  field: 'branch_id'},
        {type: 'presence',  field: 'question_id'},
        {type: 'presence',  field: 'choice_id'}
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
            model: 'SD.model.Choice',
            associationKey: 'choice',
            foreignKey: 'choice_id',
            getterName: 'getChoice',
            setterName: 'setChoice'
        }, {
            type: 'belongsTo',
            model: 'SD.model.Branch',
            associationKey: 'branch',
            foreignKey: 'branch_id',
            getterName: 'getBranch',
            setterName: 'setBranch'
        }
    ],
    proxy: {
        type: 'rest',
        url : '/rest/conditions'
    }
});