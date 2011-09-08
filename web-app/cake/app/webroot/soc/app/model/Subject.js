Ext.define('Soc.model.Subject', {
    extend: 'Ext.data.Model',
    fields: [
        {name: 'id',         type: 'int'},
        {name: 'phone_num',  type: 'string'},
        {name: 'first_name', type: 'string'},
        {name: 'last_name',  type: 'string'},
        {name: 'device_id',  type: 'string'}
    ],
    proxy: {
        type: 'rest',
        url : '/rest/subjects',
        reader: {
            type: 'json',
            record: 'Subject'
        },
        writer: {
            type: 'json',
            root: 'Subject'
        }
    }
});