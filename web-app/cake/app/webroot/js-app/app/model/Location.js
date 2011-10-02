Ext.define('SD.model.Location', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',    useNull: true},
        {name: 'subject_id',    type: 'int',    useNull: true},
        {name: 'longitude',     type: 'float',  useNull: true},
        {name: 'latitude',      type: 'float',  useNull: true},
        {name: 'accuracy',      type: 'float',  useNull: true},
        {name: 'created',       type: 'date',   dateFormat: 'Y-m-d H:i:s'},
        'subject'
    ],
    proxy: {
        type: 'rest',
        url : '/rest/locations'
    }
});