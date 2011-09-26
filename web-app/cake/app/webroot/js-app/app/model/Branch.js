Ext.define('SD.model.Branch', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'question_id',    type: 'int'},
        {name: 'next_q',    type: 'int'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/branches',
        reader: {
            type: 'json',
            record: 'Branch'
        }
    }
});