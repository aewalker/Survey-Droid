Ext.define('SD.model.Call', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',    useNull: true},
        {name: 'subject_id',    type: 'int',    useNull: true},
        {name: 'contact_id',    type: 'string',    useNull: true},
        {name: 'type',          type: 'int',    useNull: true},
        {name: 'duration',      type: 'int',  useNull: true},
        {name: 'created',       type: 'date',   dateFormat: 'Y-m-d H:i:sO'},
        'subject'
    ],
    proxy: {
        type: 'rest',
        url : '/rest/calls'
    }
});