Ext.define('SD.model.Call', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',    useNull: true},
        {name: 'subject_id',    type: 'int',    useNull: true},
        {name: 'contact_id',    type: 'int',    useNull: true},
        {name: 'type',          type: 'int',    useNull: true},
        {name: 'duration',      type: 'int',  useNull: true},
        {name: 'created',       type: 'date',   dateFormat: Ext.Date.patterns.ISO8601Long},
        'subject'
    ],
    proxy: {
        type: 'rest',
        url : '/rest/calls'
    }
});