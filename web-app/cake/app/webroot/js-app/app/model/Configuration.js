Ext.define('SD.model.Configuration', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',    type: 'int'},
        {name: 'opt',    type: 'string'},
        {name: 'c_key',    type: 'string'},
        {name: 'c_value',    type: 'string'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/configurations',
        reader: {
            type: 'json',
            record: 'Configuration'
        }
    }
});