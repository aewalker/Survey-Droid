Ext.define('SD.model.Location', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',            type: 'int',    useNull: true},
        {name: 'subject_id',    type: 'int',    useNull: true},
        {name: 'longitude',     type: 'float',  useNull: true},
        {name: 'latitude',      type: 'float',  useNull: true},
        {name: 'radius',        type: 'float',  useNull: true},
        {name: 'created',       type: 'date',   dateFormat: Ext.Date.patterns.ISO8601Long},
        'subject'
    ],
    proxy: {
        type: 'rest',
        url : '/rest/locations'
    }
});