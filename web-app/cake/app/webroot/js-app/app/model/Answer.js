Ext.define('SD.model.Answer', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',  useNull: true},
        {name: 'question_id',   type: 'int',  useNull: true},
        {name: 'subject_id',    type: 'int',  useNull: true},
        {name: 'ans_type',      type: 'int',  useNull: true},
        {name: 'ans_text',      type: 'int',  useNull: true},
        {name: 'ans_value',     type: 'int',  useNull: true},
        {name: 'created',       type: 'date',   dateFormat: 'Y-m-d H:i:s'},
        'survey_id',
        'question',
        'subject',
        'choices'
    ],
    proxy: {
        type: 'rest',
        url : '/rest/answers'
    }
});