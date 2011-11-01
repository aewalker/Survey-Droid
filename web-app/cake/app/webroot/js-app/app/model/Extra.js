Ext.define('SD.model.Extra', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',    useNull: true},
        {name: 'subject_id',    type: 'int',    useNull: true},
        {name: 'type',          type: 'int',    useNull: true},
        {name: 'data',          type: 'string', useNull: true},
        {name: 'created',       type: 'date',   dateFormat: 'Y-m-d H:i:s'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/extras'
    }
});