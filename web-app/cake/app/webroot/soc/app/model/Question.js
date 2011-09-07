Ext.define('Soc.model.Question', {
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
    belongsTo: 'Soc.model.Survey', // Each question belongs to a survey
    proxy: {
        type: 'rest',
        url : '/rest/questions',
        reader: {
            type: 'json',
            record: 'Question'
        }
    }
});